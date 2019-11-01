# -*- coding: utf-8 -*-
"""
Created on Fri Nov  1 09:53:43 2019

@author: cleartonic
"""

import pandas as pd
import os
pd.options.display.max_rows = 999
THIS_FILENAME = os.path.basename(__file__)
THIS_PATHNAME = os.path.dirname(__file__)

'''
For this script, drop in a log file for either bw1 or bw2
Into the log subdirectory relative to this file
Name the file "bw.log" or "bw2.log"

Argument for bw or bw2 to pass in, default bw

Returns a dataframe sorted by Pokemon count
Use "df" in console, or methods such as "df.head()" "df.describe()"

There are some pokemon outliers at the top of the lists,
solely because of rivals having similar Pokemon (they are excluded from placement history, but
appear in the main game)
'''

if not os.path.exists(os.path.join("log")):
    os.mkdir(os.path.join("log"))


def analyze(version='bw'):
    if not (version  == 'bw' or version == 'bw2'):
        print("Error on version")
    else:
        with open(os.path.join(THIS_PATHNAME,'ref','%s_trainers.txt' % version),'r') as file:
            data = file.readlines()
        bw2_ref = [x.strip() for x in data]
        
        with open(os.path.join(THIS_PATHNAME,'log','%s.log' % version),'r') as file:
            data = file.readlines()
            
        idx = data.index("--Trainers Pokemon--\n")
        if version == 'bw':
            data = data[idx:idx+616]
        if version == 'bw2':
            data = data[idx:idx+816]
        
        log_trainers = [x.strip() for x in data]
        
        
        
        poke_dict = {}
        for x in log_trainers:
            try:
                num = x.split(" (")[0].replace("#","")
                if num in bw2_ref:
                    temp = x.split(") - ")[1].split(", ")
                    pokes = [x.split(" Lv")[0].strip() for x in temp]
                    
                    for poke in pokes:
                        if poke in poke_dict:
                            poke_dict[poke] = poke_dict[poke] + 1
                        else:
                            poke_dict[poke] = 1
            except:
                pass
            
        df = pd.DataFrame(poke_dict,index=[0]).T
        df.columns = ['num']
        df = df.sort_values(by='num',ascending=False)
        return df

print('''This script will take a log file and output a dataframe sorted by main trainer Pokemon distribution.
      For this script, drop in a log file in this directory for either bw1 or bw2, name the file "bw.log" or "bw2.log"''')
try:
    version = input("Input version, either bw or bw2\n")
    df = analyze(version)
    print(df)
    print("Number of Pokemon: %s" % (df.shape[0]))
except:
    print("Error, put in proper version & make sure log file is present & named correctly.")    