package awesome.team.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import awesome.team.domain.User;


@Mapper
public interface UserMapper {
	
	User selectByUserName(@Param(value="userName")String userName);
	
	void updateTokenByUserName(
			@Param(value="token")String token,
			@Param(value="userName")String userName);
	
	//			@Param(value="newPassword")String newPassword,

	void insertUser(@Param(value="userName")String userName,
			@Param(value="password")String password);

}
