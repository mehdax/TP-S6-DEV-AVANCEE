package org.univ_paris8.iut.montreuil.qdev.tp2025.gr07jeuquizz.tps6.DAO;

import java.sql.Connection;
import java.util.List;


public abstract class DAO<T> {

    protected Connection connection;


    public DAO(Connection conn) {
        this.connection = conn;
    }


    public abstract boolean create(T obj);


    public abstract T find(int id);


    public abstract List<T> findAll();

    public abstract boolean update(T obj);


    public abstract boolean delete(T obj);
}
