import time

import requests
import json



def processPatientData(ALL_PATIENT_DATA):
    """
    Drop the patients that dont have cholesterol data to make a new array which have cholesterol data.
    """
    CHOLESTEROL_ALL_PATIENT_DATA = []
    for patient in ALL_PATIENT_DATA:
        try:
            valid = patient["2093-3"]
            CHOLESTEROL_ALL_PATIENT_DATA.append(patient)
        except KeyError:
            pass
    print("Total patients now is:"+str(len(CHOLESTEROL_ALL_PATIENT_DATA)))

    """
    Find the different frequencies of each code
    """
    THRESH_PERCENTAGE = 60
    code_frequency = {}
    code_to_disp = {}
    ##Initialise all codes in all patients to be 0
    ##Also make a code to display name map
    for patient in CHOLESTEROL_ALL_PATIENT_DATA:
        patient_codes = list(patient)
        for code in patient_codes:
            code_frequency[code] = 0
            if code != "ID":
                code_to_disp[code] = patient[code]['name']

    ##Save the code_to_disp map as it might be useful later
    with open('output_files/healthCode_to_Name_map.json', 'w') as filehandle:
        json.dump(code_to_disp, filehandle)


    # Now iterate through the patients again and add +1 to whatever code they have
    for patient in CHOLESTEROL_ALL_PATIENT_DATA:
        patient_codes = list(patient)
        for code in patient_codes:
            try:
                code_frequency[code] += 1
            except KeyError:
                pass

    ##Print out the codes that more than THRESH_PERCENTAGE% of patients have
    chosen_codes = []
    print("The chosen conditions are:")
    for key, value in code_frequency.items():
        # Use the number of IDs since that reflects the number of total patients
        if key != "ID":
            percentage = (value / int(code_frequency["ID"])) * 100
            if (percentage >= THRESH_PERCENTAGE):
                print(str(percentage) + "% of patients have " + code_to_disp[key])
                chosen_codes.append(key)

    """
    Now drop any patient who doesnt have these specific codes.
    """
    temp = []
    patients_removed=set()
    for patient in CHOLESTEROL_ALL_PATIENT_DATA:
        addFlag = True
        for code in chosen_codes:
            try:
                patient[code]
            except KeyError:
                patients_removed.add(patient["ID"])
                addFlag= False
        if(addFlag):
            temp.append(patient)
    CHOLESTEROL_ALL_PATIENT_DATA = temp
    print(str(len(patients_removed))+" Number of patients dropped")
    print("Remaining number of patients that can be put to the database: "+str(len(CHOLESTEROL_ALL_PATIENT_DATA)))

    """
    Delete the other codes that a patient has.
    """
    for patient in CHOLESTEROL_ALL_PATIENT_DATA:
        unwanted = set(patient)-set(chosen_codes)
        for unwanted_key in unwanted:
            #Make sure we dont delete the ID
            if unwanted_key!="ID":
                del patient[unwanted_key]

    """
    Save the CHOLESTEROL_ALL_PATIENT_DATA
    """
    with open('output_files/arrayOfTotal_CHOLESTEROL_Patients.json', 'w') as filehandle:
        json.dump(CHOLESTEROL_ALL_PATIENT_DATA, filehandle)

def main():

    #Time how long this takes
    start_time = time.time()

    print("\nProcessing Patient Data running -")
    with open('output_files/ALL_PATIENTS_DATA.json') as json_file:
        ALL_PATIENT_DATA = json.load(json_file)
        processPatientData(ALL_PATIENT_DATA)

    print("Taken--- %s seconds ---" % (time.time() - start_time))

if __name__ == "__main__":
    main()