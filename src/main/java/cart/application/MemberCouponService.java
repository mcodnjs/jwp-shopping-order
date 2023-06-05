package cart.application;

import cart.db.repository.CouponRepository;
import cart.db.repository.MemberCouponRepository;
import cart.domain.coupon.Coupon;
import cart.domain.coupon.MemberCoupon;
import cart.domain.member.Member;
import cart.dto.coupon.MemberCouponResponse;
import cart.exception.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static cart.exception.ErrorCode.ALREADY_ISSUED_COUPON;
import static cart.exception.ErrorCode.ALREADY_USED_COUPON;

@Service
@Transactional(readOnly = true)
public class MemberCouponService {

    private final CouponRepository couponRepository;
    private final MemberCouponRepository memberCouponRepository;

    public MemberCouponService(final CouponRepository couponRepository, final MemberCouponRepository memberCouponRepository) {
        this.couponRepository = couponRepository;
        this.memberCouponRepository = memberCouponRepository;
    }

    public void add(final Member member, final Long couponId) {
        Coupon coupon = couponRepository.findById(couponId);
        if (memberCouponRepository.existByMemberIdAndCouponId(member.getId(), couponId)) {
            throw new BadRequestException(ALREADY_ISSUED_COUPON);
        }
        memberCouponRepository.save(new MemberCoupon(member, coupon));
    }

    public List<MemberCouponResponse> getMemberCoupons(final Long memberId) {
        List<MemberCoupon> memberCoupons = memberCouponRepository.findAllByMemberId(memberId);
        return memberCoupons.stream()
                .map(MemberCouponResponse::from)
                .collect(Collectors.toList());
    }

    public void useMemberCoupon(final Member member, final Long couponId) {
        MemberCoupon memberCoupon = memberCouponRepository.findByMemberIdAndCouponId(member.getId(), couponId);
        memberCoupon.checkOwner(member);
        if (!memberCoupon.canUse()) {
            throw new BadRequestException(ALREADY_USED_COUPON);
        }
        MemberCoupon updatedMemberCoupon = memberCoupon.markAsUsed();
        memberCouponRepository.update(updatedMemberCoupon);
    }
}
