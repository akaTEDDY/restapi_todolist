package com.practice.restapi_todolist.repository;

import com.practice.restapi_todolist.domain.Item;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PostgresItemRepository implements ItemRepository {

    private SqlSession sqlSession;

    @Autowired
    public PostgresItemRepository(SqlSession sqlSession) {
        this.sqlSession = sqlSession;
    }

    @Override
    public boolean createItem(Item item) {
        return sqlSession.insert("Item.createItem", item) > 0 ? true : false;
    }

    @Override
    public boolean updateItem(Item item) {
        return sqlSession.update("Item.updateItem", item) > 0 ? true : false;
    }

    @Override
    public boolean deleteItem(long id) {
        return sqlSession.delete("Item.deleteItem", id) > 0 ? true : false;
    }

    @Override
    public List<Item> findItemByUserNick(String userNick) {
        return sqlSession.selectList("Item.findItemByUserNick", userNick);
    }

    @Override
    public Item findItemById(long id) {
        return sqlSession.selectOne("Item.findItemById", id);
    }


    @Override
    public boolean checkItem(long id) {
        return (int)sqlSession.selectOne("Item.checkItem", id) > 0 ? true : false;
    }

    @Override
    public boolean checkMember(String usernick) {
        return (int)sqlSession.selectOne("Item.checkMember", usernick) > 0 ? true : false;
    }

    @Override
    public List<Item> findAllItems() {
        return sqlSession.selectList("Item.findAllItems");
    }
}
