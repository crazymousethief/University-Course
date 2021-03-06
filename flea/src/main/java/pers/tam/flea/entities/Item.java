package pers.tam.flea.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.data.rest.core.config.Projection;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import pers.tam.flea.repositories.UserRepository;

import javax.persistence.*;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Projection(name = "simple", types = {Item.class})
interface ItemSimpleProjection {

    Long getId();

    String getName();

    double getPrice();

    double getOriginalPrice();

    Address getLocation();

    List<Image> getImages();

    User getSeller();

    ItemState getItemState();
}

@Projection(name = "detail", types = {Item.class})
interface ItemDetailProjection extends ItemSimpleProjection {

    String getDescription();

    Date getCreatedDate();

    Category getCategory();

    @Value("#{@itemOrderRepository.countByItemIdAndOrderStateNotIn(target.id, 'REJECTED,CANCELED'.split(','))}")
    Long getOrdersCount();
}

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class Item extends Model {

    private String name;

    private double price;

    private double originalPrice;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private Address location;

    @OneToOne
    private Category category;

    @ManyToOne(cascade = CascadeType.ALL)
    private User seller;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Image> images;

    @Column(columnDefinition = "TEXT")
    private String description;

    @OneToMany(cascade = CascadeType.ALL)
    private Collection<Comment> comments;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
    private List<ItemOrder> orders;

    @Enumerated(EnumType.STRING)
    private ItemState itemState = ItemState.SELLING;
}


@Component
@RepositoryEventHandler
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class ItemEventHandler {
    private final UserRepository userRepository;

    @HandleBeforeCreate
    public void handleItemCreate(Item item) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername());
        item.setItemState(ItemState.SELLING);
        item.setSeller(user);
    }
}