package com.travelsystem.config;

import com.travelsystem.logic.services.UserSevice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true) //I used preauthorize
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {


    @Autowired
    private final UserSevice userSevice;

    public WebSecurityConfig(UserSevice userSevice) {
        this.userSevice = userSevice;
    }




    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.requiresChannel()
                .requestMatchers(r -> r.getHeader("X-Forwarded-Proto") != null)
                .requiresSecure();


//        http
//                .authorizeRequests()
//                .antMatchers("/register").permitAll();
        http
                .authorizeRequests()
                .antMatchers("/**/css/**", "/**/js/**", "/**/images/**").permitAll()
                .antMatchers("/login").permitAll()
                .antMatchers("/forgot").permitAll()
                .antMatchers("/registerUser").permitAll()
//                .anyRequest().authenticated()

                .and()
                .authorizeRequests()
                .antMatchers("/find/**").hasAnyAuthority("ADMIN","USER")

                .antMatchers("**/secure/**").hasAuthority("ADMIN")
                .antMatchers("/deleteUser/**").hasAnyRole("ADMIN")
                .antMatchers("/giveRights/**").hasAnyRole("ADMIN")
                .antMatchers("/removeRights/**").hasAnyAuthority("ADMIN")
                .antMatchers("/giveRights/**").hasAnyAuthority("ADMIN")
.anyRequest().authenticated()
                .and()
                .exceptionHandling().accessDeniedPage("/")
                .and()
                .formLogin()
                    .loginPage("/login")
                    .permitAll()
                .and()
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/login");



    }


    @Autowired
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userSevice)
                .passwordEncoder(passwordEncoder());

    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


}
