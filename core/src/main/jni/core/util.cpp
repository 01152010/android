char crc(char* ptr, int len){
    char crc;
    crc = 0;
    for (int i = 0; i < len; i++){
        crc ^= ptr[i];
        for (int j = 0; j < 8; j++){
            if ((crc & 0x01) != 0){
                crc = (char)((crc >> 1) ^ 0x8C);
            }else{
                crc >>= 1;
            }
        }
    }
    return crc;
}