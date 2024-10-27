package dev.yeseong0412.authtemplate.domain.user.presentation

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/test")
class TestsController {
    @GetMapping("/check")
    fun sayhello() : String{
        return "hello 채팅앱"
    }
}