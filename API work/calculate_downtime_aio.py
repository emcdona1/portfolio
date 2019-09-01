import requests
import sys
import time as ti
from datetime import datetime, timedelta, time, date

MAX_QUERY_RATE = 2/3
DATE_FORMAT = '%Y-%m-%d'
TIME_FORMAT = '%H:%M:%S'
WAGE = 13


def get_employees():
    """Sends one API request to Teachworks, which returns a JSON of the first 50 active employees."""
    url = "https://api.teachworks.com/v1/employees"
    querystring = {"per_page": "50", "status": "active"}
    headers = {
        'Authorization': "Token token=st_live_651KTn9gHjNFByD1rfOKpw",
        'cache-control': "no-cache",
        'Postman-Token': "360552a6-6f43-48a1-8646-c6ff15938dd8"
    }
    employees = requests.request(
        "GET", url, headers=headers, params=querystring).json()
    return employees


def filter_out_staff(employees):
    """Removes all non-teachers and test objects from the employees object."""
    for idx, e in enumerate(employees):
        if(e['employee_type'] != 'Teacher'):
            del employees[idx]
        if('Test' in e['first_name']):
            del employees[idx]


def simplify(employees):
    teachers = []
    for i in employees:
        teachers.append(
            {'id': i['id'], 'first': i['first_name'], 'last': i['last_name']})
    
    return teachers


employees = get_employees()
filter_out_staff(employees)
teachers = simplify(employees)

# user inputs & setup
# TODO: Add data validation for start_date and end_date
start_date = input(
    'Enter the start date of the period (inclusive), format yyyy-mm-dd: ')
end_date = input(
    'Enter the end date of the period (inclusive), format yyyy-mm-dd: ')
output_filename = start_date + '_to_' + end_date + '-downtime.csv'
querystring = {'per_page': str(100), 'employee_id': None,
               'from_date[gte]': start_date, 'from_date[lte]': end_date, 'page': 1}
headers = {'Authorization': "Token token=st_live_651KTn9gHjNFByD1rfOKpw",
           'Postman-Token': "6eb804be-0642-4b72-8bac-71b51e16e9fc,6ec863cb-cdf9-4aae-ba69-4b23bd8bb62b",
           'cache-control': "no-cache"}


def send_get(query, head):
    """Send GET request to API and print request status to console."""
    url = "https://api.teachworks.com/v1/lessons"
    request_time = datetime.now()
    response = requests.request(
        "GET", url, headers=head, params=query)

    if response.status_code == 200:
        print("Request successful at " + str(request_time))
        response = response.json()
    else:
        print("Request failed, status " + str(response.status_code))
        response = {}

    ti.sleep(MAX_QUERY_RATE)
    return response


def get_teacher_lessons(teacherID):
    """Execute GET requests until all lessons for a teacher (in the given date range) have been returned. TW specifies that the maximum number of lessons that can be returned is 100."""
    page = 1
    querystring['employee_id'] = str(teacherID)
    querystring['page'] = str(page)
    lessons = []

    # send first query
    response = send_get(querystring, headers)

    # until response returns empty, process response & request subsequent pages
    while len(response) > 0:
        # TODO: could I remove this for loop and just do a += ? Test this later
        for les in response:
            lessons.append(les)

        # request next page of lessons
        page += 1
        querystring['page'] = str(page)
        response = send_get(querystring, headers)

    return lessons


def dictionary_of_dates():
    """Create a dictionary with one tuple per day, each of which contains a list to track time usage."""
    new_dict = {}
    start = datetime.strptime(start_date, DATE_FORMAT).date()
    end = datetime.strptime(end_date, DATE_FORMAT).date()
    curr = start
    one_day = timedelta(days=1)
    while curr <= end:
        # list with 'x' for each value, indexed [0 - 287] (corresponds to 00:00 - 23:55 in 5-min increments)
        new_dict[curr] = ['x'] * 288
        curr += one_day
    return new_dict


# set up
results = {}

# open file to write
f = open(output_filename, mode='w')
f.write('Teacher Name, Date, Downtime (minutes)\n')

for t in teachers:
    # list of all lessons
    lessons = get_teacher_lessons(t['id'])
    print()

    # dictionary one tuple per day, each filled with a list of 'x's
    downtime = dictionary_of_dates()

    for i in lessons:
        # get lesson details
        day = datetime.strptime(i['from_date'], DATE_FORMAT
                                ).date()
        start_time = datetime.strptime(i['from_time'], TIME_FORMAT).time()
        end_time = datetime.strptime(i['to_time'], TIME_FORMAT).time()
        status = 'x'
        if i['status'] == 'Cancelled':
            status = 'x'
        elif i['status'] == 'Attended' or i['status'] == 'Scheduled':
            status = 'a'
        elif 'No show' in i['name'] or 'no show' in i['name'] or 'No Show' in i['name']:
            status = 'n'
        else:  # last-min cancellation
            status = 'l'

        # for all lessons except properly canceled, update the status in downtime (in 5-min increments)
        if status != 'x':
            curr_time = datetime(1, 1, 1, start_time.hour, start_time.minute)
            while curr_time.time() < end_time:
                idx = int(curr_time.hour * 12 + curr_time.minute / 5)
                downtime[day][idx] = status
                curr_time += timedelta(seconds=300)
    
    total_downtime = 0
    # for each day in the time range, go through each 5 min block and count downtime
    for d in downtime:
        downtime_count = 0
        end_of_day_count = 0
        started = False
        for time in downtime[d]:
            if started:
                if time == 'x' or time == 'l': # if a downtime after started
                    downtime_count += 1
                    end_of_day_count += 1
                elif time == 'a' or time == 'n':
                    end_of_day_count = 0
            else: # start the day when you first encounter a lesson or no-show
                if time == 'a' or time == 'n':
                    started = True
        downtime_count -= end_of_day_count
        downtime_count *= 5
        f.write(t['first'] + ' ' + t['last'] + ',' + date.strftime(d, '%A %d %b %Y') + ', ' +
                str(downtime_count) + '\n')

        total_downtime += downtime_count

    f.write(t['first'] + ' ' + t['last'] +
            ',TOTAL TIME (minutes),' + str(total_downtime) + '\n')
    f.write(t['first'] + ' ' + t['last'] + ',TOTAL DOWNTIME PAY,' +
            '${0: 5.2f}'.format(total_downtime / 60 * WAGE) + '\n')
    f.write('\n')

f.close()

# TODO: Save console output to a log file

print('Downtime information saved to file: ' + output_filename)
