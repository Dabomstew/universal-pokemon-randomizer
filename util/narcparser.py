import argparse, sys, os

def main():
    try:
        with open(sys.argv[1],"rb") as f:
            word = f.read(4)
            while word != b'BTAF':
                word = f.read(4)
            table_length = read_word(f)
            file_count = read_word(f)
            file_offsets = []
            for i in range(file_count):
                file_offsets.append(read_word(f))
                f.seek(4,os.SEEK_CUR)
                print(str(i) + ": ",end='')
                print_hex_word(file_offsets[i])
            f.seek(-4,os.SEEK_CUR)
            file_offsets.append(read_word(f))
            word = f.read(4)
            while word != b'GMIF':
                word = f.read(4)
            file_table_length = read_word(f)
            file_table_offset = f.tell()
            print("File table length: ",end='')
            print_hex_word(file_table_length)
            print("File table offset: ",end='')
            print_hex_word(file_table_offset)
            readline = ""
            print("Ready to search. Enter help for list of commands")
            while readline != "quit":
                readline = input('Enter command: ')
                args = readline.split()
                if args[0] == "help":
                    list_commands()
                elif args[0] == "sr": # Search
                    if len(args) < 2:
                        print("Usage: sr x")
                        print("x should be a hexadecimal number")
                    else:
                        if args[1][:2] == "0x":
                            target = int(args[1][2:],16)
                        else:
                            target = int(args[1],16)
                        upper_byte = 0
                        if target % 2**32 > 2**8:
                            upper_byte = target >> 8
                            target = target & 0xFF
                        for i in range(file_count):
                            f.seek(file_table_offset + file_offsets[i])
                            local_offset_in_file = read_word(f)
                            first_global_offset = f.tell() + local_offset_in_file
                            while local_offset_in_file & 0xFFFF != 0xFD13 and f.tell() - 4 < first_global_offset:
                                offset_in_file = f.tell() + local_offset_in_file
                                return_position = f.tell()
                                f.seek(offset_in_file)
                                data = 0
                                while data != 0x02002F and f.tell() < file_table_offset + file_offsets[i+1]:
                                    data = read_byte(f)
                                    if data == target:
                                        if upper_byte == 0:
                                            print("Found target value in file " + str(i) + " at offset ",end='')
                                            print_hex_word(f.tell())
                                        else:
                                            data = read_byte(f)
                                            f.seek(-1,os.SEEK_CUR)
                                            if data == upper_byte:
                                                print("Found target value in file " + str(i) + " at offset ",end='')
                                                print_hex_word(f.tell())
                                    if data == 0x2F:
                                        f.seek(-1,os.SEEK_CUR)
                                        data = read_word(f)
                                        if data != 0x02002F:
                                            f.seek(-3,os.SEEK_CUR)
                                f.seek(return_position)
                                local_offset_in_file = read_word(f)
                elif args[0] == "msr": # Multiple search
                    if len(args) < 2:
                        print("Usage: msr x [y z ...]")
                        print("x, y, z ... should be hexadecimal numbers")
                    else:
                        target = []
                        upper_byte = []
                        for i in range(1,len(args)):
                            if args[i][:2] == "0x":
                                target.append(int(args[i][2:],16))
                            else:
                                target.append(int(args[i],16))
                            if target[i-1] % 2**32 > 2**8:
                                upper_byte.append(target[i-1] >> 8)
                                target[i-1] = target[i-1] & 0xFF
                            else:
                                upper_byte.append(0)
                        for i in range(file_count):
                            found_in_file = []
                            f.seek(file_table_offset + file_offsets[i])
                            local_offset_in_file = read_word(f)
                            first_global_offset = f.tell() + local_offset_in_file
                            while local_offset_in_file & 0xFFFF != 0xFD13 and f.tell() - 4 < first_global_offset:
                                offset_in_file = f.tell() + local_offset_in_file
                                return_position = f.tell()
                                f.seek(offset_in_file)
                                data = 0
                                while data != 0x02002F and f.tell() < file_table_offset + file_offsets[i+1]:
                                    data = read_byte(f)
                                    for j,t in enumerate(target):
                                        if data == t:
                                            if upper_byte[j] == 0:
                                                found_in_file.append((t,f.tell()))
                                            else:
                                                data = read_byte(f)
                                                f.seek(-1,os.SEEK_CUR)
                                                if data == upper_byte[j]:
                                                    found_in_file.append((t + (upper_byte[j] << 8),f.tell()-1))
                                    if data == 0x2F:
                                        f.seek(-1,os.SEEK_CUR)
                                        data = read_word(f)
                                        if data != 0x02002F:
                                            f.seek(-3,os.SEEK_CUR)
                                f.seek(return_position)
                                local_offset_in_file = read_word(f)
                            if len(found_in_file) >= 1:
                                target_copy = target.copy()
                                for (val,_) in found_in_file:
                                    for j,t in enumerate(target_copy):
                                        if val == t + (upper_byte[j] << 8):
                                            target_copy.remove(t)
                                if len(target_copy) == 0:
                                    print("Found in file " + str(i) + ": ")
                                    for (val,offset) in found_in_file:
                                        print_hex(val,'')
                                        print(" at offset ",end='')
                                        print_hex_word(offset)
                elif args[0] == "rsr": # Raw search
                    if len(args) < 2:
                        print("Usage: rsr x")
                        print("x should be a hexadecimal number")
                    else:
                        if args[1][:2] == "0x":
                            target = int(args[1][2:],16)
                        else:
                            target = int(args[1],16)
                        upper_byte = 0
                        if target % 2**32 > 2**8:
                            upper_byte = target >> 8
                            target = target & 0xFF
                        for i in range(file_count):
                            f.seek(file_table_offset + file_offsets[i])
                            while f.tell() < file_table_offset + file_offsets[i+1]:
                                data = read_byte(f)
                                if data == target:
                                    if upper_byte == 0:
                                        print("Found target value in file " + str(i) + " at offset ",end='')
                                        print_hex_word(f.tell())
                                    else:
                                        data = read_byte(f)
                                        f.seek(-1,os.SEEK_CUR)
                                        if data == upper_byte:
                                            print("Found target value in file " + str(i) + " at offset ",end='')
                                            print_hex_word(f.tell())
                elif args[0] == "rmsr": # Raw multiple search
                    if len(args) < 2:
                        print("Usage: rmsr x [y z ...]")
                        print("x, y, z ... should be hexadecimal numbers")
                    else:
                        target = []
                        upper_byte = []
                        for i in range(1,len(args)):
                            if args[i][:2] == "0x":
                                target.append(int(args[i][2:],16))
                            else:
                                target.append(int(args[i],16))
                            if target[i-1] % 2**32 > 2**8:
                                upper_byte.append(target[i-1] >> 8)
                                target[i-1] = target[i-1] & 0xFF
                            else:
                                upper_byte.append(0)
                        for i in range(file_count):
                            found_in_file = []
                            f.seek(file_table_offset + file_offsets[i])
                            while f.tell() < file_table_offset + file_offsets[i+1]:
                                data = read_byte(f)
                                for j,t in enumerate(target):
                                    if data == t:
                                        if upper_byte[j] == 0:
                                            found_in_file.append((t,f.tell()))
                                        else:
                                            data = read_byte(f)
                                            f.seek(-1,os.SEEK_CUR)
                                            if data == upper_byte[j]:
                                                found_in_file.append((t + (upper_byte[j] << 8),f.tell()-1))
                            if len(found_in_file) >= 1:
                                target_copy = target.copy()
                                for (val,_) in found_in_file:
                                    for j,t in enumerate(target_copy):
                                        if val == t + (upper_byte[j] << 8):
                                            target_copy.remove(t)
                                if len(target_copy) == 0:
                                    print("Found in file " + str(i) + ": ")
                                    for (val,offset) in found_in_file:
                                        print_hex(val,'')
                                        print(" at offset ",end='')
                                        print_hex_word(offset)
                elif args[0] == "lsf": # List file
                    if len(args) < 2:
                        print("Usage: lsf fileno")
                    else:
                        print("Searching file " + args[1] + "!")
                        f.seek(file_table_offset + file_offsets[int(args[1])])
                        local_offset_in_file = read_word(f)
                        first_global_offset = f.tell() + local_offset_in_file
                        while local_offset_in_file & 0xFFFF != 0xFD13 and f.tell() - 4 < first_global_offset:
                            offset_in_file = f.tell() + local_offset_in_file
                            print("Relative offset: 0x" + '%X' % local_offset_in_file + " read at 0x" + '%X' % (f.tell() - 4),end='')
                            print(", global offset: 0x" + '%X' % offset_in_file)
                            return_position = f.tell()
                            f.seek(offset_in_file)
                            data = 0
                            while data != 0x02002F and f.tell() < file_table_offset + file_offsets[int(args[1])+1]:
                                data = read_byte(f)
                                print_hex_byte(data,'','')
                                print(' ',end='')
                                if data == 0x2F:
                                    f.seek(-1,os.SEEK_CUR)
                                    data = read_word(f)
                                    if data == 0x02002F:
                                        print_hex_byte(0x00,'','')
                                        print(' ',end='')
                                        print_hex_byte(0x02,'','')
                                        print(' ',end='')
                                        print_hex_byte(0x00,'','')
                                        print(' ',end='')
                                    else:
                                        print_hex_byte(0x2F,'','')
                                        print(' ',end='')
                                        f.seek(-3,os.SEEK_CUR)
                            print("\n")
                            f.seek(return_position)
                            local_offset_in_file = read_word(f)
                elif args[0] == "fof": # File offset
                    if len(args) < 2:
                        print("Usage: fof fileno")
                    else:
                        print("Offset for file " + args[1] + ": 0x" + '%X' % (file_table_offset + file_offsets[int(args[1])]))
                elif args[0] == "srs": # Search static
                    if len(args) < 2:
                        print("Usage: srs index")
                    else:
                        if args[1][:2] == "0x":
                            target = int(args[1][2:],16)
                        else:
                            target = int(args[1],16)
                        for i in range(file_count):
                            f.seek(file_table_offset + file_offsets[i])
                            local_offset_in_file = read_word(f)
                            first_global_offset = f.tell() + local_offset_in_file
                            while local_offset_in_file & 0xFFFF != 0xFD13 and f.tell() - 4 < first_global_offset:
                                offset_in_file = f.tell() + local_offset_in_file
                                return_position = f.tell()
                                f.seek(offset_in_file)
                                data = 0
                                while data != 0x02002F and f.tell() < file_table_offset + file_offsets[i+1]:
                                    data = read_byte(f)
                                    if data == 0x2A:
                                        f.seek(-1,os.SEEK_CUR)
                                        data = read_word(f)
                                        if data == 0x8000002A:
                                            item = read_halfword(f)
                                            if item == target:
                                                print("Found item in file " + str(i) + " at offset 0x" + '%X' % (f.tell() - 2))
                                        else:
                                            f.seek(-3,os.SEEK_CUR)
                                    if data == 0x2F:
                                        f.seek(-1,os.SEEK_CUR)
                                        data = read_word(f)
                                        if data != 0x02002F:
                                            f.seek(-3,os.SEEK_CUR)
                                f.seek(return_position)
                                local_offset_in_file = read_word(f)
                elif args[0] == "lsai": # List all items
                    if len(args) == 3:
                        range_start = int(args[1],16)
                        range_end = int(args[2],16) # 6A1564C
                    else:
                        range_start = 0
                        range_end = file_count
                    with open("itemprices.txt","w") as it:
                        for i in range(range_start, range_end):
                            f.seek(file_table_offset + file_offsets[i])
                            it.write("{" + str(i) + ", " + str(int.from_bytes(f.read(2),byteorder='little')) + "},\n")
                            #print("Item 0x" + '%X' % i + " price: " + str(int.from_bytes(f.read(2),byteorder='little') * 10))
    except IndexError:
        print("Error: No file specified")
        sys.exit(2)
    except IOError:
        print("Error: File does not exist")
        sys.exit(2)
    
def list_commands():
    print("")
    print("List of commands:")
    print("  help: You are here")
    print("  sr: Search for a single hex value")
    print("  msr: Search for multiple hex values, separated by spaces")
    print("  rsr: Search for a single hex value, ignoring file structure")
    print("  rmsr: Search for multiple hex values, separated by spaces, ignoring file structure")
    print("  srs: Search for a static item (hex value). As a side effect, also lists hidden items")
    print("  lsf: Search for a specific file (decimal value) and list its contents")
    print("  fof: Show the base offset for a file")
    print("  quit: Exit the program")
    print("")
    
def print_hex(hex_value,endsymbol='\n',prefix='0x'):
    print(prefix + "{:X}".format(hex_value),end=endsymbol)
    
def print_hex_byte(hex_value,endsymbol='\n',prefix='0x'):
    print(prefix + "{:0>2X}".format(hex_value),end=endsymbol)
    
def print_hex_word(hex_value,endsymbol='\n',prefix='0x'):
    print(prefix + "{:0>8X}".format(hex_value),end=endsymbol)
    
def read_byte(file):
    return int.from_bytes(file.read(1),byteorder='little')
    
def read_halfword(file):
    return int.from_bytes(file.read(2),byteorder='little')
    
def read_word(file):
    return int.from_bytes(file.read(4),byteorder='little')
    
if __name__ == "__main__":
    main()