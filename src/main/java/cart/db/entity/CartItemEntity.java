package cart.db.entity;

public class CartItemEntity {

    private final Long id;
    private final Long memberId;
    private final Long productId;
    private final int quantity;

    public CartItemEntity(final Long id, final Long memberId, final Long productId, final int quantity) {
        this.id = id;
        this.memberId = memberId;
        this.productId = productId;
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }

    public Long getMemberId() {
        return memberId;
    }

    public Long getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }
}
