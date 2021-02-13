import time

import requests
import json

def getPatientIDList(NUM_PATIENTS):
    patientIDList = []
    root_url = 'https://fhir.monash.edu/hapi-fhir-jpaserver/fhir/'
    patients_url = root_url + "Patient"
    #next_page = True
    next_url = patients_url
    count_page = 0
    count_patient = 0

    while count_patient<NUM_PATIENTS: #Only get 500 Patients from the server
        data = requests.get(url=next_url).json()

        #next_page = False
        links = data['link']
        for i in range(len(links)):
            link = links[i]
            if link['relation'] == 'next':
                #next_page = True
                next_url = link['url']
                count_page += 1

        entries = data['entry']
        for entry in entries:
            count_patient += 1
            patientIDList.append(entry['resource']['id'])

    print("Number of IDs: " + str(len(patientIDList)))
    ##Dump the contents out to a file
    with open('output_files/arrayOfIDs.json', 'w') as filehandle:
        json.dump(patientIDList, filehandle)

def main():

    NUM_PATIENTS = 1000
    #Time how long this takes
    start_time = time.time()


    print("\nGetting Patient ID List Running-")
    getPatientIDList(NUM_PATIENTS)

    print("Taken--- %s seconds ---" % (time.time() - start_time))

##Run the thing
if __name__ == "__main__":
    main()