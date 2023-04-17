package com.Xeton.Project;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class Xeton 
{
		@Autowired 
		 JdbcTemplate jdbc;
		 
		 @GetMapping("/api/signup/insert")
		 public String signup_info(String mobileno,String password,String email)throws SQLException {
		  if(mobileno!=null) {
		  Pattern p=Pattern.compile("(0/91)?[6-9][0-9]{9}");
		  Matcher m=p.matcher(mobileno); 
		  System.out.println(mobileno);
		  if(m.find()==true) {  
		     int i= jdbc.update("insert into signup_info(mobile_no,password) values(?,?)",new Object[] {mobileno,password}); 
		      if(i>0)
		  return "data inserted Successfully";
		  }
		  else 
		  return "enter valid mobile number"; 
		  }
		  
		  
		  else {  
		  try {
		   Class.forName("com.mysql.cj.jdbc.Driver");
		 Connection con=DriverManager.getConnection("jdbc:mysql://localhost:3306/Hotelmanagement","root","Bharat@0426");
		 Statement stm=con.createStatement();
		 String query="select * from  signup_info where email_id='"+email+"' ";
		 ResultSet rs=stm.executeQuery(query);
		 
		 if(rs.next()) {
		  String pwd=rs.getString("password");
		  if(pwd.equals(password))  
		   return "you already signup with this email try another one";
		 }
		  else {  
		   PreparedStatement stm1= con.prepareStatement("insert into signup_info (email_id,password)values(?,?)");
		   stm1.setString(1, email);
		   stm1.setString(2, password);
		    int i=stm1.executeUpdate(); 
		    if(i>0)
		     return "your signup is successful";
		  }  
		 }
		 
		    catch (Exception e) {      
		     e.printStackTrace();
		    }
		  }
		  return "null";   
		 }

		 @GetMapping( "/api/Hm/login")
		 public String login(String mobileno,String password,String email)
		 {
			 try {
			 Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con= DriverManager.getConnection
			 ("jdbc:mysql://localhost:3306/Hotelmanagement","root","Bharat@0426");
			Statement stmt=con.createStatement();
			if(email!=null)
			{
			String query="select password from signup_info where email_id='"+email+"'";
			ResultSet rs=stmt.executeQuery(query);
			if(rs.next()) {
				String pwd= rs.getString("password");
				if(pwd.equals(password))

					return "login Successful";
				else 
					return "password incorrect";
				
			}
			else
			{
				return "you are not registered user,please resgister first";
			}
			}
			else if(mobileno!=null)
			{
				String query1="select password from signup_info where mobile_no='"+mobileno+"'";
				ResultSet rs1=stmt.executeQuery(query1);
				if(rs1.next()) {
					String pwds= rs1.getString("password");
					if(pwds.equals(password))
					{
						return "loged Successfully";
					}
					else {
						return "password incorrect";
					}  
				}
				else
					return "Id is not register,please resgister first";
					
			}
		 }
			 catch(Exception e) 
			 {e.printStackTrace();}
		    return"";
		 }

		@GetMapping("/api/forget_password")
		public  String forgetPassword(String email)
		{
			 Random randn=new Random();
			  int rand=randn.nextInt(10000);
		   int i=jdbc.update("insert into forget_password values(?,?)",new Object[] {rand,email});
		   
		      String data=email;
			  if(i>0)
			  {
			  SendMail.email("raobharatsingh35@gmail.com", "snfafqflwlbffwet",data, 
					  "otp to reset password",Integer.toString(rand));
			  return"your otp is succesfully sended";
			  }
			  return "otp";
		}
		
//		@GetMapping("/api/otp-confirmation")
//		public String confirmOtp(int otp)
//		{
//	       int i=jdbc.update("select*from forget_Password where otp='"+otp+"'");
//	       if(i>0)
//	       {
//	    	   int data=i.getInt("otp");
//	    	   if(data==otp)
//	    		   return "Otp is correct";
		
//	       incomplete  }      
//	       }
	    	   
		@GetMapping("/api/otp-confirmation")
		public static String confirmOtp(int otp)
		{
		 try {
		  Class.forName("com.mysql.cj.jdbc.Driver");
		 Connection con1= DriverManager.getConnection("jdbc:mysql://localhost:3306/Hotelmanagement", "root", "Bharat@0426");
		  Statement st=con1.createStatement();
		  //int s=Integer.parseInt(otp);
		  String query="select*from forget_password where otp='"+otp+"'" ;
		  ResultSet rs1=st.executeQuery(query);
		  if(rs1.next())
		  {
		    int data=rs1.getInt("otp"); 
		   if(data==otp)   
		      return "Otp is correct";  
		   
		  }
		 }
		 catch (ClassNotFoundException | SQLException e) {
		  // TODO Auto-generated catch block
		  e.printStackTrace();
		 }
		 return "otp not found please enter correct opt ";
		 
		}

		@GetMapping("/api/Reset/Password")
		public String passwordReset(String email,String password)
		{
		  int i=jdbc.update("update signup_info set password='"+password+"' where email_id='"+email+"'");
		  if(i>0)  
			  return"your password updated successfully"; 
		  
		  return "data not inserted";
			 }     
		
	 @GetMapping("/api/AddRooms/insert")
		 public String addRoom (String roomno,String roomtype,String roomstatus,
				 String roomprice,String roomavailability)
		 {
			 int i= jdbc.update("insert into add_Rooms values(?,?,?,?,?)",new Object[]
					 {roomno,roomtype,roomstatus,roomprice,roomavailability});
			if(i>0)
				return "data inserted";
		 
		     return "null";
	     }
		
	@GetMapping("/api/Rooms/get")
		 public List<Map<String,Object>> getRooms(String roomno,String roomtype,String roomstatus,
					 String roomprice,String roomavailability)
		 {
		    List<Map<String,Object>> add_rooms=jdbc.queryForList("select *from add_rooms");
		 
		     return add_rooms;
		 }


	@GetMapping("/api/searchRooms")
	public List<Map<String,Object>> searchRooms(String roomtype,String roomavailability)
	{
		 List<Map<String,Object>> roomsearch = null;
		 
		 if(roomtype!=null && roomavailability==null||roomavailability.isEmpty())
		 {
			 roomsearch=jdbc.queryForList("select *from add_rooms where room_type='"+roomtype+"'");
		 }
	 else if (roomtype!=null && roomavailability!=null)
		 {
		   roomsearch =jdbc.queryForList("select *from add_rooms where room_type ='"+roomtype+"'and room_availability= '"+roomavailability+"'");
	    }
		 return roomsearch;
		  
	}

	 @GetMapping("/api/AddEmployees/insert")
	 public String addEmployee(String employeename,String valid_id,String valid_idno,String gender,Integer age,
			 String designation,Integer salary,String mobileno)
	 {
		 int i1= jdbc.update("insert into add_employee values(?,?,?,?,?,?,?,?)",new Object[]
				 {employeename,valid_id,valid_idno,gender,age,designation,salary,mobileno});
		if(i1>0)
			return "data inserted";
		else 
			return "please insert data";
	    
	 }
	 
	 
	 @GetMapping("/api/EmployeeInfo")
	 public List<Map<String,Object>> employeeInfo(String allemployee,String employeename)
	 {
		 List<Map<String,Object>> employee_info = null;
		 
		 if(employeename==null|| employeename.isEmpty()&&employeename==""&&allemployee!=null)
		 {
			 employee_info=jdbc.queryForList("select *from add_employee");
		 }
	 else if (allemployee==null|| allemployee.isEmpty()&&allemployee==""&&employeename!=null)
		 {
	  	 employee_info=jdbc.queryForList("select *from add_employee where employee_name='"+employeename+"'");
	     }
		 return employee_info;
	 }
	 
	 
	 
	 @GetMapping("/api/CustomerForm/insert")
	 public String customerInfo(String customername,String valid_idname,String valididnu,String gender,String age,
			 String roomno,String roomtype,String state,String country,
			 String mobileno,String amountpaid,String checkin,String checkout)
	 {
		 int i1= jdbc.update("insert into new_customer_form values(?,?,?,?,?,?,?,?,?,?,?,?)",new Object[]
				 {customername,valid_idname,valididnu,gender,age,roomno,roomtype,state,
		 				 country,mobileno,amountpaid,checkin});
		 int i2= jdbc.update("insert into customer_info values(?,?,?,?,?,?,?,?,?,?,?,?,?)",new Object[]
				 {customername,valid_idname,valididnu,gender,age,roomno,roomtype,state,
		 				 country,mobileno,amountpaid,checkin,checkout});
		if(i1>0)
		{	
			int j= jdbc.update("update add_rooms set room_availability='Booked' where room_no='"+roomno+"'");
			if(j>0)
			{
				return "data inserted";
			}
		}
	 
	     return "null";
	 }
	  
	 @GetMapping("/api/Checkout1")
	 public List<Map<String,Object>> checkout(String customername)
	 {
	 List<Map<String,Object>> checkout = null;
		 
		 if(customername!=null)
		 {
			 checkout=jdbc.queryForList("select room_no,room_type,check_in,check_out from "
			 		+ "customer_info  where customer_name='"+customername+"'");
		 }
		 return checkout;
	 }
	 
	 
	 @GetMapping("/api/Checkout2")
	 public String checkout2(String customername, String roomno, 
			  String checkin,String checkout)
	 {
		
		int k= jdbc.update("update customer_info set check_out='"+checkout+"'where room_no='"
		 		+roomno+"' and customer_name='"+customername+"'");
		
		if(k>0)
		{
			 int i=jdbc.update("update add_Rooms set room_availability='unBooked' "
				 		+ "where room_no='"+roomno+"'");
				 
				 int j=jdbc.update("delete from new_customer_form where room_no='"+roomno+"' "
				 		+ "and customer_name='"+customername+"'");
				 if(i>0&&j>0)
					 return "checkout successful";
		}
		else
			return " insert data carefully";
		return "";
	 }
	 


	 @GetMapping("/api/customerInfo")
	 public List<Map<String,Object>> customerInfo(String allcustomer,String customername)
	 {
		 List<Map<String,Object>> new_customer_form = null;
		 
		 if(customername==null|| customername.isEmpty()&&customername==""&&allcustomer!=null)
		 {
			   new_customer_form=jdbc.queryForList("select *from customer_info");
		 }
	 else if (allcustomer==null|| allcustomer.isEmpty()&&allcustomer==""&&customername!=null)
		 {
			 new_customer_form=jdbc.queryForList("select *from  where customer_name='"+customername+"'");
	     }
		 return new_customer_form;
	 }
	 
	} 


