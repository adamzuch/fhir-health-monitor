import time

import pandas as pd
import json
import numpy as np

def getHumanAndMLdictionaries(PATIENT_DATA):
    HUMAN_VIEW=[]
    ML_VIEW =[]
    for patient in PATIENT_DATA:
        nHPatient={}
        nMLPatient={}
        for key, val in patient.items():
            if key!="ID":
                preKey = key

                nHKey = str(key) + "/" + str(val['name'])
                nMLKey = str(key)

                nHValue = val['value']
                nMLValue = val['value']
                #Add in the new key and value
                nHPatient[nHKey] = nHValue
                nMLPatient[nMLKey] = nMLValue
            else:
                nMLPatient[key] = val
                nHPatient[key]=val

        HUMAN_VIEW.append(nHPatient)
        ML_VIEW.append(nMLPatient)
    return HUMAN_VIEW , ML_VIEW

def main():

    #Time how long this takes
    start_time = time.time()
    print("\nProcessing Patient Health Data into viewable format running - ")
    with open("output_files/arrayOfTotal_CHOLESTEROL_Patients.json") as json_file:
        PATIENT_DATA = json.load(json_file)
    HUMAN_VIEW, ML_VIEW = getHumanAndMLdictionaries(PATIENT_DATA)

    """
    Save the machine learning view dictionary so that more non-health related data can be added
    """
    with open("ALL_PATIENTS_HEALTH_DATA_ML.json", "w") as write_file:
        json.dump(ML_VIEW, write_file)

    print("Taken--- %s seconds ---" % (time.time() - start_time))

if __name__ == "__main__":
    main()