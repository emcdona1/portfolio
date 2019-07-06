import requests
import sys

# default file name
sys.argv.append('teacherlist.tsv')

# argument check
assert (len(sys.argv) > 1), "Output file name required"


def get_employees():
    """Sends one API request to Teachworks, which returns a JSON of the first 50 active employees."""
	url = "https://api.teachworks.com/v1/employees"
    querystring = {"per_page": "50", "status": "active"}
    headers = {
        'Authorization': "<<add token here>>",
        'cache-control': "no-cache",
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

def write_file(teachers):
	"""Takes in the employee JSON and outputs a file of teacher ID's and names (TSV by default), formatted: [teacher id] \\t [first name] [last name] \\n"""
    f = open(sys.argv[1], mode='w')
    max = len(teachers) - 1
    for idx, t in enumerate(teachers):
        output_line = str(t['id']) + '\t' + \
            t['first_name'] + '\t' + t['last_name']
        if idx < max:
            output_line += '\n'
        f.write(output_line)
    f.close()

employees = get_employees()
filter_out_staff(employees)
write_file(employees)
