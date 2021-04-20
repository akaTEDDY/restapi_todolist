package com.practice.restapi_todolist;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.restapi_todolist.domain.Item;
import com.practice.restapi_todolist.exception.CustomException;
import com.practice.restapi_todolist.service.ItemService;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Date;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ItemControllerTest {

    @Autowired
    WebApplicationContext context;

    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ItemService itemService;

    @Before
    public void setUpMockMvc() {
        this.mvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void create() throws Exception {
        Item item = new Item();
        item.setTitle("test");
        item.setNote("test");
        item.setStatus("to_do");
        item.setRegDate(new Date());
        item.setDueDate(new Date());
        item.setUserNick("test");

        String strContent = objectMapper.writeValueAsString(item);

        mvc.perform(post("/item").content(strContent).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void create_NotExistUserNick() throws Exception {
        Item item = new Item();
        item.setTitle("test");
        item.setNote("test");
        item.setStatus("to_do");
        item.setRegDate(new Date());
        item.setDueDate(new Date());
        item.setUserNick("sfsdfsadfsfdfdf");

        String strContent = objectMapper.writeValueAsString(item);

        mvc.perform(post("/item").content(strContent).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void create_DueRegDate() throws Exception {
        Date now = new Date();
        Date yesterday = new Date(now.getTime() + (1000*60*60*24*-1));

        Item item = new Item();
        item.setTitle("test");
        item.setNote("test");
        item.setStatus("to_do");
        item.setRegDate(now);
        item.setDueDate(yesterday);
        item.setUserNick("test");

        String strContent = objectMapper.writeValueAsString(item);

        mvc.perform(post("/item").content(strContent).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void update() throws Exception, CustomException {
        long id = itemService.findItemByUserNick("test").get(0).getId();

        Item item = new Item();
        item.setTitle("test2");
        item.setDueDate(new Date());

        String strContent = objectMapper.writeValueAsString(item);

        mvc.perform(put("/item/" + id).content(strContent).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void update_NotExistUserNick() throws Exception, CustomException {
        Item item = new Item();
        item.setTitle("test2");
        item.setDueDate(new Date());

        String strContent = objectMapper.writeValueAsString(item);

        mvc.perform(put("/item/123123").content(strContent).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void find_pv() throws Exception, CustomException {
        mvc.perform(get("/item/smdjd"))
                .andExpect(status().isOk());
    }

    @Test
    public void find_pv_NotExistUserNick() throws Exception, CustomException {
        mvc.perform(get("/item/adafafadeaffadfaef"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void find_rp_id() throws Exception, CustomException {
        List<Item> items = itemService.findItemByUserNick("smdjd");
        Item item = items.get(0);
        String url = "/item?id=" + item.getId();

        mvc.perform(get(url))
                .andExpect(status().isOk());
    }

    @Test
    public void find_rp_id_NotExist() throws Exception, CustomException {
        String url = "/item?id=1318418394781934";

        mvc.perform(get(url))
                .andExpect(status().isNotFound());
    }

    @Test
    public void find_rp_nick() throws Exception, CustomException {
        mvc.perform(get("/item?nick=smdjd"))
                .andExpect(status().isOk());
    }

    @Test
    public void find_rp_nick_NotExist() throws Exception, CustomException { // Item은 없어도 없는대로 출력
        mvc.perform(get("/item?nick=sfsdfasfsadfsdfsafsdfsdf"))
                .andExpect(status().isOk());
    }

    @Test
    public void find_rp_idAndNick() throws Exception, CustomException {
        List<Item> items = itemService.findItemByUserNick("smdjd");
        Item item = items.get(0);

        mvc.perform(get("/item?nick=smdjd&id=" + item.getId()))
                .andExpect(status().isOk());
    }


    @Test
    public void findAllItems() throws Exception, CustomException {
        String url = "/item";

        mvc.perform(get(url))
                .andExpect(status().isOk());
    }

    @Test
    public void deleteItem() throws Exception, CustomException {
        List<Item> items = itemService.findItemByUserNick("test");
        Item item = items.get(0);
        String url = "/item/" + item.getId();

        mvc.perform(delete(url))
                .andExpect(status().isOk());
    }

    @Test
    public void deleteItem_NotExistItem() throws Exception, CustomException {
        String url = "/item/35415435134";

        mvc.perform(delete(url))
                .andExpect(status().isNotFound());
    }
}
