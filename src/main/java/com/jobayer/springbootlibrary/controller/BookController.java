package com.jobayer.springbootlibrary.controller;

import com.jobayer.springbootlibrary.entity.Book;
import com.jobayer.springbootlibrary.entity.Checkout;
import com.jobayer.springbootlibrary.responsemodels.ShelfCurrentLoansResponse;
import com.jobayer.springbootlibrary.service.BookService;
import com.jobayer.springbootlibrary.utils.ExtractJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/secure/currentloans")
    public List<ShelfCurrentLoansResponse> currentLoans(@RequestHeader(value = "Authorization") String token) throws Exception {
        String userEmail = ExtractJWT.payloadJWTExtraction(token, "\"sub\"");
        return bookService.currentLoans(userEmail);
    }

    @GetMapping("/secure/currentloans/count")
    public int currentLoansCount(@RequestHeader(value = "Authorization") String token) {
        String userEmail = ExtractJWT.payloadJWTExtraction(token, "\"sub\"");
        return bookService.currentLoansCount(userEmail);
    }

    @GetMapping("/secure/ischeckedout/byuser")
    public Boolean checkoutBookByUser(@RequestHeader(value = "Authorization") String token, @RequestParam Long bookId) {
        String userEmail = ExtractJWT.payloadJWTExtraction(token, "\"sub\"");
        return bookService.checkoutBookByUser(userEmail, bookId);
    }

    @PutMapping("/secure/checkout")
    public ResponseEntity<?> checkoutBook(@RequestHeader(value = "Authorization") String token, @RequestParam Long bookId) {
        try {
            String userEmail = ExtractJWT.payloadJWTExtraction(token, "\"sub\"");
            Book book = bookService.checkoutBook(userEmail, bookId);
            return ResponseEntity.ok(book);
        } catch (Exception e) {
            // Handle specific error messages
            if (e.getMessage().equals("Outstanding fees")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"error\": \"Outstanding fees\", \"message\": \"You have outstanding fees or overdue books. Please pay your fees and return overdue books before checking out new books.\"}");
            } else if (e.getMessage().equals("Book doesn't exist or already checked out by user")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"error\": \"Book unavailable\", \"message\": \"This book is not available for checkout. It may not exist, already be checked out by you, or have no copies available.\"}");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"Internal error\", \"message\": \"An unexpected error occurred. Please try again later.\"}");
            }
        }
    }

    @PutMapping("/secure/return")
    public void returnBook(@RequestHeader(value = "Authorization") String token,
                           @RequestParam Long bookId) throws Exception {
        String userEmail = ExtractJWT.payloadJWTExtraction(token, "\"sub\"");
        bookService.returnBook(userEmail, bookId);
    }

    @PutMapping("/secure/renew/loan")
    public void renewLoan(@RequestHeader(value = "Authorization") String token,
                          @RequestParam Long bookId) throws Exception {
        String userEmail = ExtractJWT.payloadJWTExtraction(token, "\"sub\"");
        bookService.renewLoan(userEmail, bookId);
    }

}
