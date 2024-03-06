package com.ebixcash.aayu.response;

import java.util.ArrayList;

public class ListResponse<T> {
    private ArrayList<T> list;

    public ListResponse(ArrayList<T> list) {
        this.list = list;
    }

    public ArrayList<T> getList() {
        return list;
    }

    public void setList(ArrayList<T> list) {
        this.list = list;
    }
}