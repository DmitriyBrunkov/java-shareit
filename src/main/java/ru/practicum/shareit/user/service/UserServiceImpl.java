package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.exception.EmailNotUnique;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    public User get(Long id) throws UserNotFoundException {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User " + id + " not found"));
    }

    @Override
    public User add(User user) throws UserNotFoundException, EmailNotUnique {
        return userRepository.save(user);
    }

    @Override
    public User update(User user) throws UserNotFoundException, EmailNotUnique {
        User originalUser = userRepository.findById(user.getId()).orElseThrow(() -> new UserNotFoundException("User "
                + user.getId() + " not found"));
        if (!(user.getName() == null)) {
            originalUser.setName(user.getName());
        }
        if (!(user.getEmail() == null)) {
            originalUser.setEmail(user.getEmail());
        }
        return userRepository.save(originalUser);
    }

    @Override
    public void delete(Long id) throws UserNotFoundException {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("User " + id + " not found");
        }
        userRepository.deleteById(id);
    }
}
