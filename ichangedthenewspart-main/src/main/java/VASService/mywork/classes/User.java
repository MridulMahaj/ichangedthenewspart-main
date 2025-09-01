package VASService.mywork.classes;


//Class for User
//Using attributes stored in db here for CRUD Operations to be performed in services and controller classes
public class User {
    int user_id;
    String user_name;
    String user_email;
    Long user_phone_number;
    String user_password;

    public int getUser_id(){
        return user_id;
    }

    public void setUser_id(){
        this.user_id = user_id;
    }

    public String getUser_name(){
        return user_name;
    }

    public void setUser_name(){
        this.user_name = user_name;
    }


    public String getUser_email(){
        return user_email;
    }


    public void setUser_email(){
        this.user_email = user_email;
    }



    public Long getUser_phone_number(){
        return user_phone_number;
    }


    public void setUser_phone_number(){
        this.user_phone_number = user_phone_number;
    }



    public String getUser_password() {
        return user_password;
    }
    public void setUser_password(){
        this.user_password = user_password;
    }
}
