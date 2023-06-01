package cart.ui;

import cart.application.MemberService;
import cart.dto.login.TokenResponse;
import cart.dto.login.MemberRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class MemberApiController {

    private final MemberService memberService;

    public MemberApiController(final MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/join")
    public ResponseEntity<Void> join(@RequestBody final MemberRequest memberRequest) {
        memberService.add(memberRequest);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody final MemberRequest memberRequest) {
        return ResponseEntity.ok().body(memberService.generateMemberToken(memberRequest));
    }
}
