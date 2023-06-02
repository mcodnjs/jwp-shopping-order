package cart.application;

import cart.db.dao.CartItemDao;
import cart.db.dao.ProductDao;
import cart.db.repository.ProductRepository;
import cart.domain.Product;
import cart.domain.cart.CartItem;
import cart.domain.member.Member;
import cart.dto.cart.CartItemQuantityUpdateRequest;
import cart.dto.cart.CartItemRequest;
import cart.dto.cart.CartItemResponse;
import cart.exception.CartItemException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartItemService {
    private final ProductDao productDao;
    private final ProductRepository productRepository;
    private final CartItemDao cartItemDao;

    public CartItemService(ProductDao productDao, final ProductRepository productRepository, CartItemDao cartItemDao) {
        this.productDao = productDao;
        this.productRepository = productRepository;
        this.cartItemDao = cartItemDao;
    }

    public List<CartItemResponse> findByMember(Member member) {
        List<CartItem> cartItems = cartItemDao.findByMemberId(member.getId());
        return cartItems.stream()
                .map(CartItemResponse::of)
                .collect(Collectors.toList());
    }

    public Long add(Member member, CartItemRequest cartItemRequest) {
        Product product = productRepository.findById(cartItemRequest.getProductId());
        return cartItemDao.save(new CartItem(member, product));
    }

    public void updateQuantity(Member member, Long id, CartItemQuantityUpdateRequest request) {
        CartItem cartItem = cartItemDao.findById(id);
        cartItem.checkOwner(member);

        if (request.getQuantity() == 0) {
            cartItemDao.deleteById(id);
            return;
        }

        cartItem.changeQuantity(request.getQuantity());
        cartItemDao.updateQuantity(cartItem);
    }

    public void remove(Member member, Long id) {
        CartItem cartItem = cartItemDao.findById(id);
        cartItem.checkOwner(member);

        cartItemDao.deleteById(id);
    }

    public void removeById(Member member, Long id) {
        CartItem cartItem = cartItemDao.findById(id);
        cartItem.checkOwner(member);

        cartItemDao.deleteById(id);
    }

    @Transactional
    public void removeItems(final Member member, final List<Long> ids) {
        Long count = cartItemDao.countByIdsAndMemberId(member.getId(), ids);
        if (count != ids.size()) {
            throw new CartItemException("유효하지 않은 상품 ID 입니다.");
        }
        cartItemDao.deleteByIdsAndMemberId(member.getId(), ids);
    }
}
