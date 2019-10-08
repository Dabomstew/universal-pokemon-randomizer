# -*- coding: utf-8 -*-
"""
@author: cleartonic
"""
import os, binascii, sys

# Variables

# buffer_size: how many bytes to check before/after when matching 
# on the first of list_of_items below, to scan for the others
buffer_size = 512     

# Redefine input path
if len(sys.argv) >= 2:
    input_file = sys.argv[1]
else:
    input_file = os.path.join("Pokemon Black.nds")

# below list corresponds to Castelia bottom shop first few items
list_of_items = [
 '0E00', # heal ball
 '0600', # net ball
 '0800', # nest ball
 '9000', # bridgemail s
 '8900', # greet mail
 '8A00', # favored mail 
 ]





################################
    

with open(input_file, 'rb') as f:
    data_raw = binascii.hexlify(f.read())
    
data_str = str(data_raw).upper()[2:-1] # get rid of Python "b" designation for byte strings

        
def find_all(data_str, sub):
    start = 0
    matches = []
    while start * 2 < len(data_str):
        data_slice = data_str[start:start+len(sub)] # len of sub should be 4, the length of the item we're checking
#        print(data_slice)
        finder = data_slice.find(sub)
        if finder == -1:
            pass
        else:
            matches.append(start)
        start += 2
    return matches

list_of_matches = find_all(data_str,list_of_items[0])

#list_of_matches = [x.start() for x in re.finditer(list_of_items[0], data_str)]

# then iterate over list_of_matches, and extend data by bytes (arbitrary range) to search for similar data areas
# score each "area" if others appear 


match_scores = {}
for match in list_of_matches:
    match_data = data_str[int(match-buffer_size/2):int(match+buffer_size/2)]
    match_score = 0
    for item in list_of_items:
        if item in match_data:
            match_score += 1
    # then add entry to match_scores
    match_scores[hex(int(match/2)).upper().replace("0X","").zfill(8)] = match_score
    
# Finally, filter for max scores, based on length of list_of_items
    
top_scores = [x for x in match_scores if match_scores[x] == len(list_of_items)]

