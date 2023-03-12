package com.mw_team.mw_industry_additions.utils;

public interface Cons<T> {
    void get(T t);

    interface Cons2<T,U>{
        void get(T t,U t2);
    }

    interface Cons3<T,U,V>{
        void get(T t,U t2,V t3);
    }
}
