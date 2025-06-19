package com.sajotuna.books.tag.controller.response;

import com.sajotuna.books.tag.domain.Tag;

public record TagResponse(Long id, String tagName){

    public static TagResponse from(Tag tag){
        return new TagResponse(tag.getId(), tag.getTagName());
    }
}
