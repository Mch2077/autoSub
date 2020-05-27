package awesome.team.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import awesome.team.domain.User;


@Mapper
public interface UserMapper {
	
	User selectByUserName(@Param(value="userName")String userName);
	
	User updateByUP(@Param(value="newUserName")String newUserName,
			@Param(value="newPassword")String newPassword,
			@Param(value="token")String token,
			@Param(value="oldUserName")String oldUserName,
			@Param(value="oldPassword")String oldPassword);
	
	User insertUser(@Param(value="userName")String userName,
			@Param(value="password")String password);

}
