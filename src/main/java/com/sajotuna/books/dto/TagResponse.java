package com.sajotuna.books.dto;

import com.sajotuna.books.model.Tag;

public record TagResponse(Long id, String tagName){

    public static TagResponse from(Tag tag){
        return new TagResponse(tag.getId(), tag.getTagName());
    }
}
