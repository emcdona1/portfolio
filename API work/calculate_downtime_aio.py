import requests
import sys
import time as ti
from datetime import datetime, timedelta, time, date

# globals
MAX_QUERY_RATE = 2.01/3
SUCCESSFUL = True
DATE_FORMAT = '%Y-%m-%d'
TIME_FORMAT = '%H:%M:%S'
WAGE = 13
TEACHING_START_TIME = 12*12 + int(0/5) # 12:00 p.m.
# formula: index(hour, min) = hour*12 + int(min/5)
HEADER = {}

def load_headers(filename):
    f = open('twtok.txt')
    token = 'Token token=' + f.read()
    global HEADER
    HEADER = {'Authorization': token,
            'cache-control': "no-cache"}

def get_employees(name_query=None):
    """Sends one API request to Teachworks, which returns a JSON of the first 50 active employees."""
    url = 'https://api.teachworks.com/v1/employees'
    querystring = {'per_page': '50', 'status': 'active', 'first_name': name_query}
    employees = requests.request(
        "GET", url, headers=HEADER, params=querystring).json()
    return employees

def filter_out_staff(employees):
    """Removes all non-teachers and test objects from the employees object."""
    for idx, e in enumerate(employees):
        if (e['employee_type'] == 'Staff'):
            if (e['include_as_teacher'] != '1'):
                del employees[idx]
        if 'Test' in e['first_name']:
            del employees[idx]
    return employees

def simplify_teacher_info(employees):
    teachers = []
    for i in employees:
        teachers.append(
            {'id': i['id'], 'first': i['first_name'], 'last': i['last_name']})
    
    # sort teachers by first name
    teachers.sort(key = lambda item: item.get('last'))
    teachers.sort(key = lambda item: item.get('first'))
    
    return teachers

def send_get(query):
    """Send GET request to API and print request status to console."""
    url = "https://api.teachworks.com/v1/lessons"
    request_time = datetime.now()
    response = requests.request(
        "GET", url, headers=HEADER, params=query)

    if response.status_code == 200:
        print("Request successful at " + str(request_time))
        response = response.json()
    else:
        print("Request failed, status " + str(response.status_code))
        response = {}
        global SUCCESSFUL
        SUCCESSFUL = False

    ti.sleep(MAX_QUERY_RATE)
    return response

def get_teacher_lessons(teacherID):
    """Execute GET requests until all lessons for a teacher (in the given date range) have been returned. TW specifies that the maximum number of lessons that can be returned is 100."""
    page = 1
    querystring['employee_id'] = str(teacherID)
    querystring['page'] = str(page)
    lessons = []

    # send first query
    response = send_get(querystring)

    # until response returns empty, process response & request subsequent pages
    while len(response) > 0:
        # TODO: could I remove this for loop and just do a += ? Test this later
        for les in response:
            lessons.append(les)

        # request next page of lessons
        page += 1
        querystring['page'] = str(page)
        response = send_get(querystring)

    return lessons

def create_empty_date_dictionary():
    """Create a dictionary with one tuple per day, each of which contains a list to track time usage."""
    new_dict = {}
    start = datetime.strptime(start_date, DATE_FORMAT).date() #output a datetime.date(y,m,d) object
    end = datetime.strptime(end_date, DATE_FORMAT).date() #output a datetime.date(y,m,d) object
    curr = start
    one_day = timedelta(days=1)
    while curr <= end:
        # list with 'x' for each value, indexed [0 - 287] (corresponds to 00:00 - 23:55 in 5-min increments)
        # significant numbers: 9am = 108, noon = 144, 3pm = 180, 9pm = 252
        new_dict[curr] = ['x'] * 288
        curr += one_day
    return new_dict

def get_user_input():
    # TODO: Add data validation for start_date and end_date
    start_date = input(
        'Enter the start date of the period (inclusive), format yyyy-mm-dd: ')
    end_date = input(
        'Enter the end date of the period (inclusive), format yyyy-mm-dd: ')
    output_filename = start_date + '_to_' + end_date + '-downtime.csv'

    return start_date, end_date, output_filename

def create_downtime_dictionary(lessons):
    downtime = create_empty_date_dictionary() # one tuple per day, each filled with a list of 'x's
    
    for a_lesson in lessons:
        # TODO: print a warning if a lesson contains ??? in the title
        # TODO: probably create an enum for statuses?
        # get lesson details
        lesson_day = datetime.strptime(a_lesson['from_date'], DATE_FORMAT
                                ).date()
        start_time = datetime.strptime(a_lesson['from_time'], TIME_FORMAT).time()
        end_time = datetime.strptime(a_lesson['to_time'], TIME_FORMAT).time()
        lesson_status = ''
        if a_lesson['status'] == 'Cancelled':
            lesson_status = 'x'
        elif a_lesson['status'] == 'Attended' or a_lesson['status'] == 'Scheduled':
            lesson_status = 'a'
        elif 'No show' in a_lesson['name'] or \
            'no show' in a_lesson['name'] or \
            'No Show' in a_lesson['name']:
            lesson_status = 'n'
        else:  # last-min cancellation
            lesson_status = 'l'

        # for all lessons except properly canceled, update the status in downtime (in 5-min increments)
        if lesson_status != 'x':
            curr_time = datetime(1, 1, 1, start_time.hour, start_time.minute)
            while curr_time.time() < end_time:
                index = int(curr_time.hour * 12 + curr_time.minute / 5)
                if downtime[lesson_day][index] != 'a': #don't override 'attended'/'scheduled'
                    downtime[lesson_day][index] = lesson_status
                curr_time += timedelta(seconds=300) # add 5 minutes, repeat
    return downtime

def count_downtime(downtime):
    total_downtime = 0
    # for each day in the time range, go through each 5 min block and count downtime
    for d in downtime:
        downtime_count = 0
        end_of_day_count = 0
        started = False
        for i in range(TEACHING_START_TIME, len(downtime[d])): # start at TEACHING_START_TIME
            if started:
                if downtime[d][i] == 'x' or downtime[d][i] == 'l': # if a downtime after started
                    downtime_count += 1
                    end_of_day_count += 1
                elif downtime[d][i] == 'a' or downtime[d][i] == 'n':
                    end_of_day_count = 0
            else: # start the day when you first encounter a lesson or no-show
                if downtime[d][i] == 'a' or downtime[d][i] == 'n':
                    started = True
        downtime_count -= end_of_day_count
        downtime_count *= 5
        f.write(t['first'] + ' ' + t['last'] + ',' + date.strftime(d, '%A %d %b %Y') + ', ' +
                str(downtime_count) + '\n')

        total_downtime += downtime_count
    return total_downtime

def write_teacher_downtime_total_to_file(t, total_downtime):
    f.write(t['first'] + ' ' + t['last'] +
            ',TOTAL TIME (minutes),' + str(total_downtime) + '\n')
    f.write(t['first'] + ' ' + t['last'] + ',TOTAL DOWNTIME PAY,' +
            '${0: 5.2f}'.format(total_downtime / 60 * WAGE) + '\n')
    f.write('\n')

if __name__ == '__main__':
    load_headers('twtox.txt')
    employees = get_employees()
    teachers = filter_out_staff(employees)
    teachers = simplify_teacher_info(teachers)

    # user inputs & setup
    # TODO: Add data validation for start_date and end_date
    start_date, end_date, output_filename = get_user_input()

    querystring = {'per_page': str(100), 'employee_id': None,
                'from_date[gte]': start_date, 'from_date[lte]': end_date, 'page': 1}
    results = {}

    # open file to write
    f = open(output_filename, mode='w')
    f.write('Teacher Name, Date, Downtime (minutes)\n')

    for t in teachers:
        # list of all lessons
        lesson_list = get_teacher_lessons(t['id'])
        print('.')

        downtime = create_downtime_dictionary(lesson_list)
        total_downtime = count_downtime(downtime)

        write_teacher_downtime_total_to_file(t, total_downtime)

    f.close()

    print(SUCCESSFUL)
    if not SUCCESSFUL:
        print('One or more request to the server was unsuccessful')
    print('Downtime information saved to file: ' + output_filename)
