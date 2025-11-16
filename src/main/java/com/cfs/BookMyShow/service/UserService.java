package com.cfs.BookMyShow.service;

import com.cfs.BookMyShow.dto.UserDto;
import com.cfs.BookMyShow.exception.ResourceNotFoundException;
import com.cfs.BookMyShow.model.User;
import com.cfs.BookMyShow.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;


    public UserDto createUser(UserDto userDto)
    {
        User user=mapToEntity(userDto);
        User saveUser=userRepository.save(user);



        return mapToDto( saveUser);
    }

    public UserDto getUserById(Long id)
    {
        User user=userRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("user not found"));
        return mapToDto(user);

    }
    public List<UserDto> getAllUsers()
    {
        List<User> users=userRepository.findAll();

        return users.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());

    }
    public UserDto updateUser(Long id,UserDto userDto)
    {
        User user=userRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("User not found"));

        User saveUser=userRepository.save(user);
        return mapToDto(saveUser);
    }

    public void deleteUser(Long id)
    {
        User user=userRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("User not found"));

        userRepository.delete(user);
    }

    private User mapToEntity(UserDto userDto)
    {
        User user=new User();
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        user.setPhoneNumber(userDto.getPhoneNumber());

        return user;
    }
    private UserDto mapToDto(User user)
    {
        UserDto userDto=new UserDto();

        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        userDto.setPhoneNumber(user.getPhoneNumber());

        return userDto;
    }
}
