package com.practice.restapi_todolist.repository;

import com.practice.restapi_todolist.domain.Item;

import java.util.List;

public interface ItemRepository {
    boolean createItem(Item item);
    boolean updateItem(Item item);
    boolean deleteItem(long id);
    List<Item> findItemByUserNick(String userNick);
    Item findItemById(long id);
    boolean checkItem(long id);
    boolean checkMember(String usernick);
    List<Item> findAllItems();
}
