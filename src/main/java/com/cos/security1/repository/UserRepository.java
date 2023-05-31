package com.cos.security1.repository;

import com.cos.security1.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

//CRUD 함수를 JpaRePository가 들고 있음.
//@Repository라는 어노테이션이 없어도 IoC된다. 이유는 JapRepository를 상속했기 때문이다.
public interface UserRepository extends JpaRepository<User, Integer> {

    //findBy 규칙 -> Username문법
    // select*from user where username = ? << 이 쿼리문이 호출된다.
    public User findByUsername(String username); // Jpa Query methods

    //select * from user where email = ?
//    public User findByEmail();
}
