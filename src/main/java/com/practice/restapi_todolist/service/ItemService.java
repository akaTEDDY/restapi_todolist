package com.practice.restapi_todolist.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.restapi_todolist.domain.Item;
import com.practice.restapi_todolist.exception.CustomBadRequestException;
import com.practice.restapi_todolist.exception.CustomException;
import com.practice.restapi_todolist.exception.CustomNotFoundException;
import com.practice.restapi_todolist.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ItemService {

    private ItemRepository itemRepository;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    private SimpleDateFormat dateParse = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public List<Item> createItem(Item item) throws CustomException, ParseException {
        // 로그인한 member의 nick을 usernick으로 설정
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        item.setUserNick(userDetails.getUsername());

        // Date 설정 + Date formatting
        item.setRegDate(sdf.parse(sdf.format(new Date())));
        item.setDueDate(sdf.parse(sdf.format(item.getDueDate())));

        // RegDate와 DueDate가 오늘인 경우 DueDate가 먼저 설정되는 경우가 있음
        if(dateParse.format(item.getRegDate()).equals(dateParse.format(item.getDueDate())))
            item.setRegDate(sdf.parse(sdf.format(item.getDueDate())));

        // RegDate 와 DueDate 비교
        if(item.getDueDate().before(item.getRegDate()))
            throw new CustomBadRequestException(HttpStatus.BAD_REQUEST, Map.of("RegDate", item.getRegDate().toString(), "DueDate", item.getDueDate().toString()), "완료 일정이 등록 일정보다 빠릅니다.");

        // Item 생성
        ObjectMapper mapper = new ObjectMapper();
        if(!itemRepository.createItem(item))
            new CustomBadRequestException(HttpStatus.BAD_REQUEST, mapper.convertValue(item, Map.class), "주어진 파라미터로 아이템 생성이 불가합니다.");

        // 결과 return
        List<Item> items = findItemByUserNick(item.getUserNick());
        return items;
    }

    public Item updateItem(long id, Item item) throws CustomException, ParseException {
        // id으로 findItem - A (null check)
        Optional<Item> optItem = Optional.ofNullable(findItemById(id));

        // A에 item의 값 Set (null check)
        Item i = optItem.orElse(null);
        if(i == null)
            throw new CustomNotFoundException(HttpStatus.NOT_FOUND, Map.of("id", Long.toString(id)), "입력한 id를 가진 아이템을 찾을 수 없습니다.");

        if(!item.getTitle().isEmpty())     i.setTitle(item.getTitle());
        if(!item.getNote().isEmpty())       i.setNote(item.getNote());
        if(!item.getStatus().isEmpty())    i.setStatus(item.getStatus());
        if(item.getDueDate() != null)    i.setDueDate(sdf.parse(sdf.format(item.getDueDate())));
        if(!item.getUserNick().isEmpty())    i.setUserNick(item.getUserNick());

        // Item이 가진 usernick이 존재하는 user인지 확인
        if(!itemRepository.checkMember(i.getUserNick()))
            throw new CustomNotFoundException(HttpStatus.NOT_FOUND, Map.of("usernick", i.getUserNick()), "nick " + i.getUserNick() + "을 가진 Member가 존재하지 않습니다.");

        // DueDate가 RegDate보다 뒤인지 확인
        if(i.getDueDate().before(i.getRegDate()))
            throw new CustomBadRequestException(HttpStatus.BAD_REQUEST, Map.of("RegDate", i.getRegDate().toString(), "DueDate", i.getDueDate().toString()), "완료 일정이 등록 일정보다 빠릅니다.");

        // item update 요청
        // 결과 return
        ObjectMapper mapper = new ObjectMapper();
        if(!itemRepository.updateItem(i))
            throw new CustomBadRequestException(HttpStatus.BAD_REQUEST, mapper.convertValue(item, Map.class), "주어진 파라미터로 아이템 수정이 불가합니다.");

        return i;
    }

    public boolean deleteItem(long id) throws CustomException {
        // nick이 있는 지 확인
        if(!checkItem(id))
            throw new CustomNotFoundException(HttpStatus.NOT_FOUND, Map.of("id", Long.toString(id)), "입력한 id를 가진 아이템을 찾을 수 없습니다.");

        // member delete 요청
        // 결과 return
        return itemRepository.deleteItem(id);
    }

    public List<Item> findItemByUserNick(String usernick) throws CustomException {
        // member가 있는 지 확인
        if(!checkMember(usernick))
            throw new CustomNotFoundException(HttpStatus.NOT_FOUND, Map.of("usernick", usernick), "입력한 usernick의 아이템을 찾을 수 없습니다.");

        List<Item> items = new ArrayList<>();
        List<Item> optList = itemRepository.findItemByUserNick(usernick);

        for(int i = 0; i < optList.size(); i++) {
            Optional<Item> optItem = Optional.ofNullable(optList.get(i));
            if(optItem.isPresent())
                items.add(optItem.get());
        }

        return items;
    }

    public Item findItemById(long id) throws CustomException {
        // nick이 있는 지 확인
        if(!checkItem(id))
            throw new CustomNotFoundException(HttpStatus.NOT_FOUND, Map.of("id", Long.toString(id)), "입력한 id를 가진 아이템을 찾을 수 없습니다.");

        // item find 요청
        Optional<Item> item = Optional.ofNullable(itemRepository.findItemById(id));
        return item.orElse(null);
    }

    public List<Item> findAllItems() {
        List<Item> items = new ArrayList<>();
        List<Item> optList = itemRepository.findAllItems();

        for(int i = 0; i < optList.size(); i++) {
            Optional<Item> optItem = Optional.ofNullable(optList.get((i)));
            if(optItem.isPresent())
                items.add(optItem.get());
        }

        return items;
    }

    public boolean checkItem(long id) {
        return itemRepository.checkItem(id);
    }

    public boolean checkMember(String usernick) {
        return itemRepository.checkMember(usernick);
    }
}
