package com.alibaba.test;


import com.alibaba.bean.TbItemExample;
import com.alibaba.mapper.ItemMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import java.util.List;

/*@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/spring-*.xml"})*/
public class PageHelperTest {
/*@Autowired
private ItemMapper mapper;*/
	@Test
	public void test() {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:mybatis/mybatis.xml");
		ItemMapper mapper = context.getBean(ItemMapper.class);
		
		PageHelper.startPage(1, 10);
		TbItemExample itemExample = new TbItemExample();
		List list = mapper.selectByExample(itemExample);
		PageInfo pageInfo = new PageInfo(list);
		System.out.println(pageInfo.getTotal());
	}
}
