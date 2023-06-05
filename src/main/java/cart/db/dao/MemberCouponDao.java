package cart.db.dao;

import cart.db.entity.MemberCouponDetailEntity;
import cart.db.entity.MemberCouponEntity;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class MemberCouponDao {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;

    public MemberCouponDao(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("member_coupon")
                .usingGeneratedKeyColumns("id");
    }

    public void create(final MemberCouponEntity memberCouponEntity) {
        SqlParameterSource parameterSource = new BeanPropertySqlParameterSource(memberCouponEntity);
        simpleJdbcInsert.execute(parameterSource);
    }

    public List<MemberCouponDetailEntity> findAllByMemberId(final Long memberId) {
        String sql = "SELECT * FROM member_coupon " +
                "JOIN coupon ON member_coupon.coupon_id = coupon.id " +
                "JOIN member ON member_coupon.member_id = member.id " +
                "WHERE member_id = ?";
        return jdbcTemplate.query(sql, new MemberCouponDetailEntityRowMapper(), memberId);
    }

    public Optional<MemberCouponDetailEntity> findByMemberIdAndCouponId(final Long memberId, final Long couponId) {
        String sql = "SELECT * FROM member_coupon " +
                "JOIN coupon ON member_coupon.coupon_id = coupon.id " +
                "JOIN member ON member_coupon.member_id = member.id " +
                "WHERE member_id = ? AND coupon_id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, new MemberCouponDetailEntityRowMapper(), memberId, couponId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public void update(final MemberCouponEntity memberCouponEntity) {
        String sql = "UPDATE member_coupon SET is_used = ? WHERE id = ?";
        jdbcTemplate.update(sql, memberCouponEntity.getIsUsed(), memberCouponEntity.getId());
    }

    public Boolean existByMemberIdAndCouponId(final Long memberId, final Long couponId) {
        String sql = "SELECT EXISTS(SELECT * FROM member_coupon WHERE member_id = ? AND coupon_id = ?)";
        return jdbcTemplate.queryForObject(sql, Boolean.class, memberId, couponId);
    }

    private static class MemberCouponDetailEntityRowMapper implements RowMapper<MemberCouponDetailEntity> {
        @Override
        public MemberCouponDetailEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new MemberCouponDetailEntity(
                    rs.getLong("id"),
                    rs.getLong("member.id"),
                    rs.getString("member.name"),
                    rs.getString("member.password"),
                    rs.getLong("coupon.id"),
                    rs.getString("coupon.name"),
                    rs.getInt("coupon.discount_rate"),
                    rs.getInt("coupon.period"),
                    rs.getTimestamp("coupon.expired_at").toLocalDateTime(),
                    rs.getTimestamp("issued_at").toLocalDateTime(),
                    rs.getTimestamp("expired_at").toLocalDateTime(),
                    rs.getBoolean("is_used")
            );
        }
    }
}
