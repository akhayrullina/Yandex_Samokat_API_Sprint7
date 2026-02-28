package pojo;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Courier {
    private String login;
    private String password;
    private String firstName;

    public Courier(String login, String password) {
        this.login = login;
        this.password = password;
    }
}
