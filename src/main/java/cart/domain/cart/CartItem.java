package cart.domain.cart;

import cart.domain.product.Item;
import cart.domain.product.Product;
import cart.domain.member.Member;
import cart.exception.AuthorizationException;

import java.util.Objects;

import static cart.exception.ErrorCode.NOT_AUTHORIZATION_MEMBER;

public class CartItem {
    private final Long id;
    private final Item item;
    private final Member member;

    public CartItem(Member member, Product product) {
        this.id = null;
        this.item = new Item(product);
        this.member = member;
    }

    public CartItem(Long id, int quantity, Product product, Member member) {
        this.id = id;
        this.item = new Item(product, quantity);
        this.member = member;
    }

    public void checkOwner(Member member) {
        if (!Objects.equals(this.member.getId(), member.getId())) {
            throw new AuthorizationException(NOT_AUTHORIZATION_MEMBER);
        }
    }

    public void changeQuantity(int quantity) {
        item.changeQuantity(quantity);
    }

    public Long getId() {
        return id;
    }

    public Member getMember() {
        return member;
    }

    public Item getItem() {
        return item;
    }
}
