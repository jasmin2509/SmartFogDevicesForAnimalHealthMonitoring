#!/usr/bin/env python
import os
import socket
from sense_hat import SenseHat
import time
import uuid

# Methods that turn LEDs red/blue, WORKIND: add actuators' control here

# Turn left line of LEDs red = Heating
def ledHeatingOn():
    for r in range(0,4):
        sense.set_pixel(r,0,255,0,0)
# Turn second left line of LEDs blue = Air Condition
def ledAirConditionOn():
    for r in range(0,4):
        sense.set_pixel(r,1,0,0,255)
# Turn left line of LEDs off = Heating off
def ledHeatingOff():
    for r in range(0,4):
        sense.set_pixel(r,0,0,0,0)    
# Turn second left line of LEDs off = Air Condition off
def ledAirConditionOff():
    for r in range(0,4):
        sense.set_pixel(r,1,0,0,0)

# Get the MAC address
def get_mac():
    mac_num = hex(uuid.getnode()).replace('0x', '').upper()
    mac = '-'.join(mac_num[i : i + 2] for i in range(0, 11, 2))
    return mac

# Get the CPU temperature
def getCpuTemperature():
    res = os.popen('vcgencmd measure_temp').readline()
    return (res.replace("temp=","").replace("'C\n",""))
    

# Determines the next action for temperature depending on the received string
# Possibilities for the Temperature Actions explained:
# 1. "ack":      No temperature action before, no temperature action now                -      tempAction = null
# 2. "noheat":   Heating before, heating now off, no temperature action now             -      tempAction = null, ledHeatingOff() 
# 3. "noairc":   AirCondition before, AirCondition now off, no temperature action now   -      tempAction = null, ledAirConditionOff()
# 4. "airc":     No temperature action before, climate on now                           -      tempAction = airc, ledAirConditionOn()
# 5. "warm":     AirCondition before, still AirCondition                                -      tempAction = airc
# 6. "heat":     No temperature action before, heating on now                           -      tempAction = heat, ledHeatingOn()
# 7. "cold":     Heating before, still heating                                          -      tempAction = heat
def determineTempAction(task):
    # ack means temperature is still okay, no temperature Action (tempAction = null)
    if (task == "ack"):
        return "null"
    # noheat means that temperature was too low but is okay now, so heating should be turned off
    elif (task == "noheat"):
        ledHeatingOff()
        return "null"            
    # noairc means that temperature was too high but is okay now, so AirCondition should be turned off
    elif (task == "noairc"):
        ledAirConditionOff()
        return "null"
    # airc means temperature is too high and AirCondition needs to turn on = LEDs blue, tempAction = airc
    elif (task == "airc"):
        ledAirConditionOn()
        return "airc"
    # warm means temperature was too high and is still, AirCondition stays turned on
    elif (task == "warm"):
        return "airc" 
    # heat means temperature is too low and heating needs to turn on = LEDs red, tempAction = heat
    elif (task == "heat"):
        ledHeatingOn()
        return "heat"
    # cold means temperature was too low and is still, heating stays turned on
    elif (task == "cold"):
        return "heat"
    # No answer received yet
    elif (task == "cam"):
        print ("CAMERA")
        return ""
    else:
        return ""
    

sense = SenseHat()
sense.clear()


# Variables:

# IP-Address Work Station
HOST = "192.168.0.109"
# IP-Address second work station
HOST2 = "192.168.0.107"

PORT = 8081
# String with value of last interaction. i.e. heat means that the heating ist active, so the work station knows, what was the last temperature action
tempAction = "null"


# Enable TCP Connection to Work Station
sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
connection_result = sock.connect((HOST, PORT))


# First the connection to the home work station address is tried. If it doesnt work, try the second work station address
if connection_result:
    sock.connect((HOST2, PORT))
else:
    print ("Connection successful")

# Get the MAC address
macaddress = get_mac()

# Send first only the MAC address, so that the work station can check the registry
sock.sendall(bytes(macaddress + "\n", 'UTF-8'))
receivedBytes = sock.recv(1024)

while True:        
    # Measure room temperature (Attention! Value is falsified by CPU-temperature)
    temp = sense.get_temperature_from_pressure()

    # Get CPU-temperature
    cpu_temp_celsius = float(getCpuTemperature())

    # Get falsified temperature from SenseHAT
    temp_celsius = sense.get_temperature_from_pressure()

    # Compute both temperatures in Fahrenheit
    cpu_temp_fahrenheit = (9*cpu_temp_celsius/5)+32
    temp_fahrenheit = (9*temp_celsius/5)+32

    # Compute calibrated temperature, recompute it into Celsius and round it
    temp_calibrated_fahrenheit = temp_fahrenheit - ((cpu_temp_fahrenheit - temp_fahrenheit)/5.466) 
    temp_calibrated = round(5*(temp_calibrated_fahrenheit - 32)/9 ,1)
    
    # Measure humidity and round value
    humidity = round(sense.get_humidity())

    # Cast values in String to send them
    sendstr = macaddress + " " + "connect" + " " + str(temp_calibrated) + " " + tempAction + " " + str(humidity)
    
    # Send temperaturestring to work station, add \n because Java needs it
    sock.sendall(bytes(sendstr + "\n", 'UTF-8'))
   
    # Wait for the work station's answer    
    receivedBytes = sock.recv(1024)
    received = receivedBytes.decode('UTF-8')
    tempAction = determineTempAction(received)
    if (tempAction != ""):
        i = False
         
    # Maybe necessary with a large number of R'Pis: Wait for X seconds until next measurement
    #time.sleep(X)
    

sock.close()

