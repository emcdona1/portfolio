import requests
import time as ti
from datetime import datetime, timedelta, date

# "constants"
MAX_QUERY_RATE = 2.05 / 3
DATE_FORMAT = '%Y-%m-%d'
TIME_FORMAT = '%H:%M:%S'
WAGE = 13
TEACHING_START_TIME = 12 * 12 + 0 // 5  # 12:00 p.m.
# formula: index(hour, minute) = hour*12 + minute//5 <-- integer division


class TeacherList:
    def __init__(self, name_query=None):
        # Sends one API request to Teachworks, which returns a JSON of the first 50 active employees.
        # self.lst is a list of dictionaries (each dict = 1 teacher)
        url = 'https://api.teachworks.com/v1/employees'
        teacher_querystring = {'per_page': '50', 'status': 'active', 'first_name': name_query}
        self.lst = send_get(url, teacher_querystring)
        self.filter_out_staff()
        self.simplify_teacher_info()

    def filter_out_staff(self):
        """Removes all non-teachers and test objects from the employees object."""
        # Remove test objects
        self.lst = [t for t in self.lst if not 'Test' in t['first_name']]
        # Remove non-teaching staff
        self.lst = [t for t in self.lst if not (t['employee_type'] == 'Staff' and t['include_as_teacher'] == 0)]

    def simplify_teacher_info(self):
        simplified_map = map(self.simple_teacher, self.lst)
        self.lst = list(simplified_map)
        # sort teachers alpha by first name (last name = secondary sort)
        self.lst.sort(key=lambda item: item.get('last'))
        self.lst.sort(key=lambda item: item.get('first'))

    def simple_teacher(self, teacher):
        return {'id': teacher['id'],
                'first': teacher['first_name'],
                'last': teacher['last_name']}

    def write_downtime_totals_to_file(self, filename):
        with open(filename, mode='w') as f:
            # write file headers
            f.write('Teacher Name, Date, Downtime (minutes)\n')

            for teacher in self.lst:
                totals_by_day = teacher['downtime'].downtime_totals_by_day
                for day in totals_by_day:
                    f.write(teacher['first'] + ' ' + teacher['last'] + ',' + date.strftime(day, '%A %d %b %Y') + ', ' +
                            str(totals_by_day[day]) + '\n')
                f.write(teacher['first'] + ' ' + teacher['last'] +
                        ',TOTAL TIME (minutes),' + str(teacher['downtime'].total_downtime) + '\n')
                f.write(teacher['first'] + ' ' + teacher['last'] + ',TOTAL DOWNTIME PAY,' +
                        '${0: 5.2f}'.format(teacher['downtime'].total_downtime / 60 * WAGE) + '\n')
                f.write('\n')


class Downtime:
    def __init__(self):
        """Create a dictionary -- key is a datetime.date object, value is a list of 'x's to track time usage."""
        self.downtime = {}
        self.downtime_totals_by_day = {}
        self.total_downtime = -1

        curr_day = datetime.strptime(start_date, DATE_FORMAT).date() # a datetime.date(y,m,d) object
        end_day = datetime.strptime(end_date, DATE_FORMAT).date()  # a datetime.date(y,m,d) object
        one_day = timedelta(days=1)
        while curr_day <= end_day:
            # list with 'x' for each value, indexed [0 - 287] (corresponds to 00:00 - 23:55 in 5-min increments)
            # significant numbers: 9am = 108, noon = 144, 3pm = 180, 9pm = 252
            self.downtime[curr_day] = ['x'] * 288
            curr_day += one_day

    def update_downtime(self, lesson_list):
        for a_lesson in lesson_list:
            lesson_day, start_time, end_time, lesson_status = self.parse_a_lesson(a_lesson)

            if lesson_status != 'x':
                curr_time = datetime(1, 1, 1, start_time.hour, start_time.minute)
                while curr_time.time() < end_time:
                    index = int(curr_time.hour * 12 + curr_time.minute / 5)
                    if self.downtime[lesson_day][index] != 'a':  # don't override 'attended'/'scheduled'
                        self.downtime[lesson_day][index] = lesson_status
                    curr_time += timedelta(seconds=300)  # add 5 minutes, repeat

        self.update_totals()

    def parse_a_lesson(self, lesson):
        lesson_day = datetime.strptime(lesson['from_date'], DATE_FORMAT
                                       ).date()
        start_time = datetime.strptime(lesson['from_time'], TIME_FORMAT).time()
        end_time = datetime.strptime(lesson['to_time'], TIME_FORMAT).time()
        lesson_status = ''
        if lesson['status'] == 'Cancelled':
            lesson_status = 'x'
        elif lesson['status'] == 'Attended' or lesson['status'] == 'Scheduled':
            lesson_status = 'a'
        elif 'No show' in lesson['name'] or \
                'no show' in lesson['name'] or \
                'No Show' in lesson['name']:
            lesson_status = 'n'
        else:  # last-min cancellation
            lesson_status = 'l'

        return lesson_day, start_time, end_time, lesson_status

    def update_totals(self):
        self.downtime_totals_by_day = {}
        self.total_downtime = 0
        # for each day in the time range, go through each 5 min block and count downtime
        for day in self.downtime:
            downtime_count = 0
            end_of_day_count = 0
            started = False
            for i in range(TEACHING_START_TIME, len(self.downtime[day])):  # start at TEACHING_START_TIME
                if started:
                    if self.downtime[day][i] == 'x' or self.downtime[day][i] == 'l':  # if a downtime after started
                        downtime_count += 1
                        end_of_day_count += 1
                    elif self.downtime[day][i] == 'a' or self.downtime[day][i] == 'n':
                        end_of_day_count = 0
                else:  # start the day when you first encounter a lesson or no-show
                    if self.downtime[day][i] == 'a' or self.downtime[day][i] == 'n':
                        started = True
            downtime_count -= end_of_day_count
            downtime_count *= 5
            self.downtime_totals_by_day[day] = downtime_count

            self.total_downtime += downtime_count


def user_input_and_setup():
    # TODO: Add data validation for start_date and end_date
    start_date = input(
        'Enter the start date of the period (inclusive), format yyyy-mm-dd: ')
    end_date = input(
        'Enter the end date of the period (inclusive), format yyyy-mm-dd: ')

    output_filename = start_date + '_to_' + end_date + '-downtime.csv'
    lesson_query = {'per_page': str(100), 'employee_id': None,
                          'from_date[gte]': start_date, 'from_date[lte]': end_date, 'page': None}
    success = True

    return start_date, end_date, output_filename, lesson_query, success


def load_header(filename):
    token = ''
    with open(filename, 'r') as f:
        token = 'Token token=' + f.read()
    return {'Authorization': token, 'cache-control': "no-cache"}


def send_get(url, query):
    """ Send GET request to API and print request status to console. """
    request_time = datetime.now()
    response = requests.request(
        "GET", url, headers=header, params=query)

    if response.status_code == 200:
        # print("Request successful at " + str(request_time))
        print('.')
        response = response.json()
    else:
        print("Request failed, status " + str(response.status_code))
        response = []
        global all_queries_successful
        all_queries_successful = False

    ti.sleep(MAX_QUERY_RATE)
    return response


def get_teacher_lessons(teacher_id):
    """Execute GET requests until all lessons for a teacher (in the given date range) have been returned.
    TW specifies that the maximum number of lessons per query is 100.
    Returns a list of lessons (dictionaries). """
    url = 'https://api.teachworks.com/v1/lessons'
    page = 0
    lesson_querystring['employee_id'] = str(teacher_id)
    lesson_querystring['page'] = str(page)
    lessons = []

    # until response returns empty, process response & request subsequent pages
    query_not_empty = True
    while query_not_empty:
        # run query on next page
        page += 1
        lesson_querystring['page'] = str(page)
        response = send_get(url, lesson_querystring)

        query_not_empty = len(response) > 0
        # process results, if they exist
        if query_not_empty:
            lessons.extend(response)

    return lessons


if __name__ == '__main__':
    header = load_header('twtok.txt')
    teachers = TeacherList()

    start_date, end_date, output_filename, lesson_querystring, all_queries_successful = user_input_and_setup()

    # calculate downtime for each teacher
    for t in teachers.lst:
        lesson_list = get_teacher_lessons(t['id'])
        teacher_downtime = Downtime()
        teacher_downtime.update_downtime(lesson_list)
        t['downtime'] = teacher_downtime

    teachers.write_downtime_totals_to_file(output_filename)

    print(all_queries_successful)
    if not all_queries_successful:
        print('One or more request to the server was unsuccessful')
    print('Downtime information saved to file: ' + output_filename)
