package fi.roope.fmprojectbackend.security;

import fi.roope.fmprojectbackend.filter.CustomAuthenticationFilter;
import fi.roope.fmprojectbackend.filter.CustomAuthorizationFilter;
import fi.roope.fmprojectbackend.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static fi.roope.fmprojectbackend.util.AuthUtil.*;
import static org.springframework.http.HttpMethod.*;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final UserDetailsService userDetailsService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        var customAuthenticationFilter = new CustomAuthenticationFilter(authenticationManagerBean());
        customAuthenticationFilter.setFilterProcessesUrl("/api/v1/login");

        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.authorizeRequests().antMatchers("/api/v1/login").permitAll();
        http.authorizeRequests().antMatchers("/api/v1/auth/**").permitAll();
        http.authorizeRequests().antMatchers("/api/v1/register").permitAll();
        http.authorizeRequests().antMatchers("/api/v1/activate").permitAll();
        http.authorizeRequests().antMatchers("/api/v1/route/public").permitAll();
        http.authorizeRequests().antMatchers(GET, "/api/v1/users").hasAnyAuthority(ADMIN);
        http.authorizeRequests().antMatchers(POST, "/api/v1/user/save/**").hasAnyAuthority(ADMIN);
        http.authorizeRequests().antMatchers(GET, "/api/v1/route/**").hasAnyAuthority(ADMIN, ACTIVATED); // omiin reitteihin tarvitsee olla kirjautunut ja tunnus aktiivinen
        http.authorizeRequests().antMatchers(POST, "/api/v1/route/**").hasAnyAuthority(ADMIN, ACTIVATED); // reitin luontiin tarvitsee olla kirjautunut ja tunnus aktiivinen
        //http.authorizeRequests().antMatchers(DELETE, "/api/v1/route/**").permitAll();
        http.authorizeRequests().anyRequest().authenticated();
        http.addFilter(customAuthenticationFilter);
        http.addFilterBefore(new CustomAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
