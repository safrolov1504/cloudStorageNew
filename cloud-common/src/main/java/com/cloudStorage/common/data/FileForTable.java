package com.cloudStorage.common.data;

import java.nio.file.attribute.FileTime;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class FileForTable {
    public String nameFileTable;
    public String sizeFileTable;
    public String dateCreatFileTable;
    private static String end = "<END>";
    private static String split = "<SPLIT>";
    private static long TOMB = 1024*1024;
    private static long TOKB = 1024*1024;
    private static DateFormat df = new SimpleDateFormat("dd/MM/yyyy");

    public static String getEnd() {
        return end;
    }

    public FileForTable(String nameFileTable, long sizeFileTable, FileTime dateCreatFileTable) {
        this.nameFileTable = nameFileTable;
        this.sizeFileTable = changeSize(sizeFileTable);
        this.dateCreatFileTable = changeTime(dateCreatFileTable);
    }

    private String changeTime(FileTime ft){
        return df.format(ft.toMillis());
    }

    private String changeSize(long size){
        if(size>TOMB){
            return size/TOMB + " Mb";
        }
//        else if(size>TOKB){
//            return size/TOKB + " Kb";
//        } else
            {
            return size + "Kb";
        }

    }
    public FileForTable() {
    }

    public FileForTable(String inString) {
        String[] subStr = inString.split(split);
        nameFileTable = subStr[0];
        sizeFileTable = subStr[1];
        dateCreatFileTable = subStr[2];
    }

    public String getNameFileTable() {
        return nameFileTable;
    }

    public void setNameFileTable(String nameFileTable) {
        this.nameFileTable = nameFileTable;
    }

    public String getSizeFileTable() {
        return sizeFileTable;
    }

    public void setSizeFileTable(String sizeFileTable) {
        this.sizeFileTable = sizeFileTable;
    }

    public String getDateCreatFileTable() {
        return dateCreatFileTable;
    }

    public void setDateCreatFileTable(String dateCreatFileTable) {
        this.dateCreatFileTable = dateCreatFileTable;
    }

    @Override
    public String toString() {
        return nameFileTable + split + sizeFileTable + split + dateCreatFileTable + end;
    }
}
