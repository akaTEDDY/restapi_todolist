package com.practice.restapi_todolist;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.restapi_todolist.domain.Member;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MemberControllerTest {

    @Autowired
    WebApplicationContext context;

    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Before
    public void setUpMockMvc() {
        this.mvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void create() throws Exception {
        Member member = new Member();
        member.setNick("test");
        member.setPw("test");
        member.setEmail("test@test.com");

        String strContent = objectMapper.writeValueAsString(member);

        mvc.perform(post("/member").content(strContent).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void create_nullNickPw() throws Exception {
        Member member = new Member();
        member.setPw("test");
        member.setEmail("test@test.com");

        String strContent = objectMapper.writeValueAsString(member);

        mvc.perform(post("/member").content(strContent).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void create_duplicateNick() throws Exception {
        Member member = new Member();
        member.setPw("smdjd");
        member.setPw("smdjd");
        member.setEmail("test@test.com");

        String strContent = objectMapper.writeValueAsString(member);

        mvc.perform(post("/member").content(strContent).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void update() throws Exception {
        Member member = new Member();
        member.setNick("smdjd");
        member.setPw("smdjd");
        member.setEmail("smdjd@smdjd.com");

        String strContent = objectMapper.writeValueAsString(member);

        mvc.perform(put("/member/smdjd").content(strContent).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void update_NotExistMember() throws Exception {
        Member member = new Member();
        member.setNick("smdjd");
        member.setPw("smdjd");
        member.setEmail("smdjd@smdjd.com");

        String strContent = objectMapper.writeValueAsString(member);

        mvc.perform(put("/member/sfsdfdsfsdf").content(strContent).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void update_NullMember() throws Exception {
        Member member = new Member();
        member.setNick("");
        member.setPw("smdjd");
        member.setEmail("smdjd@smdjd.com");

        String strContent = objectMapper.writeValueAsString(member);

        mvc.perform(put("/member/smdjd").content(strContent).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void findMember() throws Exception {
        mvc.perform(get("/member?nick=smdjd"))
                .andExpect(status().isOk());
    }

    @Test
    public void findMember_NotExistNick() throws Exception {
        mvc.perform(get("/member?nick=sfdsfdsasdf"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void findAllMember1() throws Exception {
        mvc.perform(get("/member"))
                .andExpect(status().isOk());
    }


    @Test
    public void findAllMember2() throws Exception {
        mvc.perform(get("/member/smdjd"))
                .andExpect(status().isOk());
    }

    @Test
    public void findAllMember2_NotExistNick() throws Exception {
        mvc.perform(get("/member/sfragsefsgsrgsf"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteMember() throws Exception {
        mvc.perform(delete("/member/test"))
                .andExpect(status().isOk());
    }

    @Test
    public void deleteMember_NotExistNick() throws Exception {
        mvc.perform(delete("/member/sfawefswfefsgathbvr"))
                .andExpect(status().isNotFound());
    }
}
