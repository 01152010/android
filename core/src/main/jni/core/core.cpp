#include "util.h"
#include "core.h"
char *send(char headH,char headL,char address,int cmdLen, char *pCmd, int dataLen, char *pData){
    char* temp = new char[2+1+1+cmdLen+dataLen+1];
    temp[0] = headH;
    temp[1] = headL;
    temp[2] = address;
    temp[3] = (char)(cmdLen+dataLen+1);
    for(int i = 4; i < cmdLen+4; i++){
        temp[i] = *(pCmd++);
    }
    for(int i = cmdLen+4; i < dataLen+cmdLen+4; i++){
        temp[i] = *(pData++);
    }
    temp[dataLen+cmdLen+4] = crc(temp+2, 1 + 1 + cmdLen + dataLen);
    return temp;
}











