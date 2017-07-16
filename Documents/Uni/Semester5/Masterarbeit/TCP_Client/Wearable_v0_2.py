#!/usr/bin/env python
import time
import socket
from microstacknode.hardware.accelerometer.mma8452q import MMA8452Q
import uuid
import math
import numpy as np
from threading import Thread 


def get_mac():
    mac_num = hex(uuid.getnode()).replace('0x','').upper()
    mac = '-'.join(mac_num[i : i + 2] for i in range(0, 11, 2))
    return mac

def getBatteryCondition():
    # WORKING: calculate battery condition as a value between 0 and 1
    return 0.5

def getBodyTemperature():
    # WORKING: get temperature from body temperature sensor
    return 39.2

# Calibration of the accelerometer:
# Calculates MovementVector from the acceleration values and subtracts the gravitational force
def calculateGravitationVector():
    global movementMeasurement
    global vectorSum
    global sampleCounterMovement
    global vectorG
    global vectorAccel
    while True:
        accel = accelerometer.get_xyz()
        x = accel['x']
        y = accel['y']
        z = accel['z']
        vectorAccel = np.array([x, y, z])
        
        # Calculate absolute value of the current movement vector
        absValueMovement = math.sqrt(x*x+y*y+z*z)

        if movementMeasurement == False:
            # No measurement started yet, start new movement
            # Check if animal is moving currently
            if (absValueMovement > (1 - accelerometerAccuracy) and absValueMovement < (1 + accelerometerAccuracy)):
                # Animal is not moving: start calibration, set vectorSum to current vector and start counter
                vectorSum = vectorAccel
                sampleCounterMovement = 1
                movementMeasurement = True
                #print ("Calibration starts")
        else:
            # Measurement already started, go on
            sampleCounterMovement += 1
            # Multiply current vector with sampleCount to provide prioritiy of newer values
            vectorSum = vectorSum + vectorAccel
                
            # Check if animal is moving currently
            if (absValueMovement > (1 - accelerometerAccuracy) and absValueMovement < (1 + accelerometerAccuracy)):
                # Animal is not moving: check if we already got enough values for calibration
                if sampleCounterMovement >= necessarySamples:
                    # Calculate gravitation vector by dividing it through the number of measured samples
                    vectorG = vectorSum/sampleCounterMovement 
                    #print ("Calibration ends")
                    # Enough values, end measurement
                    movementMeasurement = False
                    sampleCounterMovement = 0
                #else:
                    #print("Calibration runs (below counter)")
            #else:
                # Animal is moving, go on with measurements for calibration
                #print ("Calibration runs (animal moving)")
    
              
# Variables:

# IP-Address Work Station
HOST = "192.168.0.106"
# Second possible IP-Address work station
HOST2 = "192.168.0.109"
PORT = 8080

# Battery condition between 0 and 1 at which an alert should be send to the farmer
minBatteryCondition = 0.1
# Accuracy of the accelerometer calibration value between 0 and 1, the nearer the value is to 0, the more accurate works the accelerometer but also the more time is taken for measurements
accelerometerAccuracy = 0.02
# Counts the samples that are measured for the accelerometer calibration
sampleCounterMovement = 0
# Defines how much samples are necessary to validate calibration
necessarySamples = 50
# Boolean value that is set to true, if the calibration hat begun
movementMeasurement = False
# Sum of all measured vectors including priorities for the new values
vectorSum = np.array([0,0,0])

vectorAccel = np.array([0,0,0])

measureInterval = 0.5

messageWorkStation = ""

# Gravitational force vector which must be subtracted from the movement vector to get the animal movement
vectorG = np.array([0,0,0])

# Enable TCP Connection to Work Station
sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
connection_result = sock.connect((HOST, PORT))


if __name__ == '__main__':
    with MMA8452Q() as accelerometer:
        # First the connection to the home work station address is tried. If it doesnt work, try the second work station address
        if connection_result:
            sock.connect((HOST2, PORT))
        else:
            print ("Connection successful")

        # Send mac address so that the work station can check the registry
        macaddress = get_mac()
        sock.sendall(bytes(macaddress + "\n", 'UTF-8'))
        received = sock.recv(1024)
        
        # Start a new thread to continuously calibrate the accelerometer
        t = Thread(target = calculateGravitationVector, args = ())
        t.start()

        while True: 
            # Check if gravitational force vector is already computed       
            if (vectorG[0] != 0 or vectorG[1] != 0 or vectorG[2] != 0):
                vectorAnimalMovement = vectorAccel - vectorG
                xA = vectorAnimalMovement[0]
                yA = vectorAnimalMovement[1]
                zA = vectorAnimalMovement[2]
        
                # Get body temperature
                temp = round(getBodyTemperature(), 2)
                
                # Check Battery condition    
                if (getBatteryCondition() <= minBatteryCondition):
                    messageWorkStation = "lowbattery"
                else:
                    messageWorkStation = "connect"

                # Ger MAC address
                macaddress = get_mac()   
                
                # Compute the complete message as a string
                sendstr = macaddress + " " + messageWorkStation + " " + str(temp) + " " + str(xA) + " " + str(yA) + " " + str(zA)
                    
                # Send String to work station, add \n because Java needs it
                sock.sendall(bytes(sendstr + "\n", 'UTF-8'))
                   
                # Wait for the work station's answer
                received = sock.recv(1024)
                receivedstring = received.decode('UTF-8')
                
                #print (receivedstring)
        
sock.close()
