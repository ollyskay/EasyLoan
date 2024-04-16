package com.example.easyloan.model;

import com.example.easyloan.dto.MessageDto;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class MessageMapper {
    private final ModelMapper modelMapper;

    public MessageMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public Message convertToEntity(MessageDto messageDto) {
        return modelMapper.map(messageDto, Message.class);
    }
}
