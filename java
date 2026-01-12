@Entity
@Table(name = "users")
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    private String email;

    @Enumerated(EnumType.STRING)
    private Role role;
}
public enum Role {
    ADMIN, MEMBER
}
@Entity
@Table(name = "items")
public class Item {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String itemName;
    private String description;
    private String category;
    private String location;
    private LocalDate date;
    private Boolean found;

    @ManyToOne
    private User reportedBy;
}
@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByCategory(String category);
    List<Item> findByLocation(String location);
    List<Item> findByDate(LocalDate date);
}
@Service
public class ItemService {
    @Autowired
    private ItemRepository itemRepository;

    public Item create(Item item) {
        return itemRepository.save(item);
    }

    public List<Item> search(String category, String location, LocalDate date) {
        // implement dynamic filtering
    }

    public void delete(Long id) {
        itemRepository.deleteById(id);
    }
}
@RestController
@RequestMapping("/api/items")
public class ItemController {

    @Autowired
    private ItemService itemService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MEMBER')")
    public ResponseEntity<Item> create(@RequestBody Item item) {
        return ResponseEntity.ok(itemService.create(item));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Item>> search(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return ResponseEntity.ok(itemService.search(category, location, date));
    }
}
security/
├── JwtFilter.java
├── JwtUtil.java
├── SecurityConfig.java
└── CustomUserDetailsService.java
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired private CustomUserDetailsService userDetailsService;
    @Autowired private JwtFilter jwtFilter;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/auth/**").permitAll()
                .antMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
                .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
GET /api/reports/items/pdf

