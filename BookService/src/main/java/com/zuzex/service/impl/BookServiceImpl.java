package com.zuzex.service.impl;

//@Service
//@Transactional
public class BookServiceImpl {

//    private static final String BASE_URL = "https://www.googleapis.com/books/v1/volumes?q=";
//
//    private final BookRepository bookRepository;
//    private final AuthorRepository authorRepository;
//    private final UserRepository userRepository;
//    private final RestTemplate restTemplate;
//    private final ExecutorService executorService = Executors.newFixedThreadPool(3);
//    private final ObjectMapper objectMapper = new ObjectMapper();
//
//    @Autowired
//    public BookServiceImpl(BookRepository bookRepository, AuthorRepository authorRepository, UserRepository userRepository, RestTemplate restTemplate) {
//        this.bookRepository = bookRepository;
//        this.authorRepository = authorRepository;
//        this.userRepository = userRepository;
//        this.restTemplate = restTemplate;
//    }
//
//    @Override
//    public Book getBookById(Long id) {
//        return bookRepository.findById(id).orElse(null);
//    }
//
//    @Override
//    public Book getBookByTitle(String title) {
//        return bookRepository.findByTitle(title).orElse(null);
//    }
//
//    @Override
//    public List<Book> getAllBooks() {
//        return bookRepository.findAll();
//    }
//
//    @Override
//    public List<Book> getAllReadBooks() {
//        return bookRepository.findByIsReadEquals(true);
//    }
//
//    @Override
//    public List<Book> getAllNoReadBooks() {
//        return bookRepository.findByIsReadEquals(false);
//    }
//
//    @Override
//    public boolean addToReadBooks(Book book) {
//        if (!book.getIsRead()) {
//            book.setIsRead(true);
//            bookRepository.save(book);
//            return true;
//        }
//        return false;
//    }
//
//    @Override
//    public boolean deleteFromReadBooks(Book book) {
//        if (book.getIsRead()) {
//            book.setIsRead(false);
//            bookRepository.save(book);
//            return true;
//        }
//        return false;
//    }
//
//    @Override
//    public List<Book> getBooksByDate(String date) {
//        return bookRepository.findByDate(date);
//    }
//
//    @Override
//    public BookResponse getBookResponse(BookRequest bookRequest) {
//        String responseEntity = restTemplate.getForObject(BASE_URL.concat(bookRequest.getTitle()), String.class);
//        BookResponse bookResponse = new BookResponse();
//        List<AuthorResponse> authorResponseList = new ArrayList<>();
//        try {
//            JsonNode root = objectMapper.readTree(responseEntity);
//            JsonNode items = root.path("items");
//            for (JsonNode item : items) {
//                JsonNode volumeInfo = item.path("volumeInfo");
//                bookResponse.setTitle(volumeInfo.path("title").asText());
//                bookResponse.setDate(volumeInfo.path("publishedDate").asText());
//                JsonNode authorsName = volumeInfo.get("authors");
//                for (JsonNode name : authorsName) {
//                    String n = name.asText();
//                    authorResponseList.add(new AuthorResponse(n));
//                }
//                bookResponse.setAuthors(authorResponseList);
//                break;
//            }
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }
//        return bookResponse;
//    }
//
//    @Override
//    public synchronized Book createNewBook(BookResponse bookResponse, String login) {
//        Optional<Book> optionalBook = bookRepository.findByTitle(bookResponse.getTitle());
//        Optional<User> optionalUser = userRepository.findByLogin(login);
//        Book book;
//        User user;
//        if (optionalUser.isEmpty()) {
//            user = new User();
//            user.setLogin(login);
//            user.setBooks(new ArrayList<>());
//            userRepository.save(user);
//        } else {
//            user = optionalUser.get();
//        }
//        if (optionalBook.isEmpty()) {
//            book = new Book();
//            book.setTitle(bookResponse.getTitle());
//            book.setDate(bookResponse.getDate());
//            book.setAuthors(getAllAuthorsFromAuthorResponse(bookResponse));
//            book.setIsRead(false);
//            book.setUsers(new ArrayList<>());
//            book.setCreated(new Date());
//            book.setUpdated(new Date());
//            book.setStatus(Status.ACTIVE);
//            book = bookRepository.save(book);
//            user.getBooks().add(book);
//            LogDto logDto = LogDto.builder()
//                    .name(book.getTitle())
//                    .currentDate(book.getCreated())
//                    .service("google")
//                    .method("create")
//                    .entityType("book")
//                    .build();
//            userRepository.save(user);
//            System.out.println(logDto);
//            restTemplate.postForEntity("http://127.0.0.1:8086/logs/create", logDto, void.class);
//        } else {
//            book = optionalBook.get();
//            user.getBooks().add(book);
//            userRepository.save(user);
//        }
//        return book;
//    }
//
//    @Override
//    public List<Book> getBooksByAuthorId(Long id) {
//        return authorRepository.getById(id).getBooks();
//    }
//
//    @Override
//    public Author getAuthorByName(String name) {
//        return authorRepository.findByName(name);
//    }
//
//    @Override
//    public void deleteBookById(Long id) {
//        Book book = bookRepository.getById(id);
//        bookRepository.deleteById(id);
//        LogDto logDto = LogDto.builder()
//                .name(book.getTitle())
//                .currentDate(book.getCreated())
//                .service("google")
//                .method("delete")
//                .entityType("book")
//                .build();
//        restTemplate.postForEntity("http://127.0.0.1:8086/logs/create", logDto, void.class);
//    }
//
//    @Override
//    public List<Author> getAllAuthors() {
//        return authorRepository.findAll();
//    }
//
//    @Override
//    public Author getAuthorById(Long id) {
//        return authorRepository.getById(id);
//    }
//
//    @Override
//    public List<BookResponse> getAllBookResponseByAuthor(AuthorRequest authorRequest) {
//        String responseEntity = restTemplate.getForObject(BASE_URL.concat("+inauthor:").concat(authorRequest.getAuthorName()), String.class);
//        BookResponse bookResponse = null;
//        List<AuthorResponse> authorResponseList = null;
//        List<BookResponse> bookResponseList = new ArrayList<>();
//
//        try {
//            JsonNode root = objectMapper.readTree(responseEntity);
//            JsonNode items = root.path("items");
//            for (JsonNode item : items) {
//                bookResponse = new BookResponse();
//                authorResponseList = new ArrayList<>();
//                JsonNode volumeInfo = item.path("volumeInfo");
//                bookResponse.setTitle(volumeInfo.path("title").asText());
//                bookResponse.setDate(volumeInfo.path("publishedDate").asText());
//                JsonNode authorsName = volumeInfo.get("authors");
//                if (authorsName != null) {
//                    for (JsonNode name : authorsName) {
//                        String n = name.asText();
//                        authorResponseList.add(new AuthorResponse(n));
//                    }
//                }
//                bookResponse.setAuthors(authorResponseList);
//                bookResponseList.add(bookResponse);
//            }
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }
////        Gson gson = new GsonBuilder()
////                .setPrettyPrinting()
////                .registerTypeAdapter(ListBookResponse.class, new ListBookResponseDeserializer())
////                .create();
////        ListBookResponse listBookResponse = gson.fromJson(responseEntity, ListBookResponse.class);
////        List<BookResponse> bookResponseList = listBookResponse.getBookResponseList();
//        return bookResponseList;
//    }
//
//    @Override
//    public List<Book> createNewBooksByAuthor(List<BookResponse> bookResponseList, String login) {
//        AtomicInteger count = new AtomicInteger(0);
//        List<Book> resultList = new ArrayList<>();
//        CountDownLatch countDownLatch = new CountDownLatch(3);
//
//        for (int i = 0; i < 3; i++) {
//            executorService.submit(() -> {
//                while (count.get() < bookResponseList.size()) {
//                    BookResponse bookResponse = bookResponseList.get(count.getAndIncrement());
//                    System.out.println(Thread.currentThread().getName());
//                    resultList.add(createNewBook(bookResponse, login));
//                }
//                countDownLatch.countDown();
//            });
//        }
//        while (countDownLatch.getCount() != 0) {
//            try {
//                countDownLatch.await();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//        return resultList;
//    }
//
//    @Override
//    public Map<String, Long> getAllCounts() {
//        Map<String, Long> map = new HashMap<>();
//        map.put("book", bookRepository.count());
//        map.put("author", authorRepository.count());
//        return map;
//    }
//
//    private List<Author> getAllAuthorsFromAuthorResponse(BookResponse bookResponse) {
//        List<Author> authors = new ArrayList<>();
//        Author author;
//        if (bookResponse.getAuthors() != null) {
//            for (AuthorResponse response : bookResponse.getAuthors()) {
//                author = authorRepository.findByName(response.getName());
//                if (author == null) {
//                    author = new Author();
//                    author.setName(response.getName());
//                    author.setCreated(new Date());
//                    author.setUpdated(new Date());
//                    author.setStatus(Status.ACTIVE);
//
//                    authors.add(author);
//                    authorRepository.save(author);
//                    LogDto logDto1 = LogDto.builder()
//                            .name(author.getName())
//                            .currentDate(author.getCreated())
//                            .service("google")
//                            .method("create")
//                            .entityType("author")
//                            .build();
//                    System.out.println(logDto1);
//
//                    restTemplate.postForEntity("http://127.0.0.1:8086/logs/create", logDto1, void.class);
//                } else {
//                    authors.add(author);
//                }
//            }
//        }
//        return authors;
//    }
}
