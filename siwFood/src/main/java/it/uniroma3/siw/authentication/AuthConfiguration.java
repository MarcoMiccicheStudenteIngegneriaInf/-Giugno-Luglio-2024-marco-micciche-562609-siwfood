package it.uniroma3.siw.authentication;

// Importazioni necessarie per Spring Security e configurazioni di sicurezza
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

@Configuration  // Indica che questa classe contiene configurazioni di Spring
@EnableWebSecurity  // Abilita la configurazione di sicurezza web di Spring Security
public class AuthConfiguration {

    @Autowired
    private DataSource dataSource;  // Inietta il DataSource per accedere al database

    
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        // Configura l'autenticazione con JDBC, utilizzando il DataSource per accedere al database
        auth.jdbcAuthentication()
            .dataSource(dataSource)
            .authoritiesByUsernameQuery("SELECT username, role from credentials WHERE username=?")
            .usersByUsernameQuery("SELECT username, password, 1 as enabled FROM credentials WHERE username=?");
    }
    
    @Bean
    public PasswordEncoder passwordEncoder(){
        // Definisce un bean per l'encoder delle password usando BCrypt
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        // Definisce un bean per il gestore dell'autenticazione
        return authenticationConfiguration.getAuthenticationManager();
    }

    private static final String ADMIN_ROLE = "ADMIN";  // Definisce una costante per il ruolo di amministratore

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Configura la catena di filtri di sicurezza
        http
            .csrf().and().cors().disable()  // Disabilita CSRF e CORS (non raccomandato per la produzione)
            .authorizeHttpRequests()
            .requestMatchers(HttpMethod.GET, "/", "/index", "/register", "/css/**", "/images/**", "/favicon.ico","/ricette/**","/cuochi/**","/ricetta/**","/cuoco/**").permitAll()  // Permette l'accesso pubblico a specifici URL
            .requestMatchers(HttpMethod.POST, "/register", "/login").permitAll()  // Permette l'accesso pubblico alle registrazioni e login
            .requestMatchers(HttpMethod.GET, "/admin/**","/admin/all_user").hasAnyAuthority(ADMIN_ROLE)  // Richiede il ruolo di amministratore per accedere agli URL amministrativi
            .requestMatchers(HttpMethod.POST, "/admin/**").hasAnyAuthority(ADMIN_ROLE)  // Richiede il ruolo di amministratore per le richieste POST sugli URL amministrativi
            .anyRequest().authenticated()  // Richiede l'autenticazione per tutte le altre richieste
            .and()
            .formLogin()
                .loginPage("/login")  // Specifica la pagina di login personalizzata
                .permitAll()
                .defaultSuccessUrl("/success", true)  // URL di successo dopo il login
                .failureUrl("/login?error=true")  // URL di fallimento del login
            .and()
            .oauth2Login()
                .loginPage("/login")  // Specifica la pagina di login per OAuth2
                .defaultSuccessUrl("/success", true)  // URL di successo dopo il login OAuth2
                .failureUrl("/login?error=true")  // URL di fallimento del login OAuth2
            .and()
            .logout()
                .logoutUrl("/logout")  // URL di logout
                .logoutSuccessUrl("/login?logout")  // URL di successo dopo il logout
                .invalidateHttpSession(true)  // Invalida la sessione HTTP
                .deleteCookies("JSESSIONID")  // Elimina il cookie di sessione
                .permitAll();

        return http.build();  // Costruisce e ritorna la configurazione della sicurezza
    }


}
