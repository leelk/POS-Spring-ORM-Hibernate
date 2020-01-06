package lk.ijse.dep.pos.business.custom.impl;

import lk.ijse.dep.pos.business.custom.ItemBO;
import lk.ijse.dep.pos.business.exception.AlreadyExistsInOrderException;
import lk.ijse.dep.pos.dao.custom.ItemDAO;
import lk.ijse.dep.pos.dao.custom.OrderDetailDAO;
import lk.ijse.dep.pos.db.HibernateUtil;
import lk.ijse.dep.pos.dto.ItemDTO;
import lk.ijse.dep.pos.entity.Item;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ItemBOImpl implements ItemBO {

    @Autowired
    private OrderDetailDAO orderDetailDAO;
    @Autowired
    private ItemDAO itemDAO;

    @Override
    public void saveItem(ItemDTO item) throws Exception {

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            itemDAO.setSession(session);
            session.beginTransaction();
            itemDAO.save(new Item(item.getCode(),
                    item.getDescription(), item.getUnitPrice(), item.getQtyOnHand()));
            session.getTransaction().commit();
        }
    }

    @Override
    public void updateItem(ItemDTO item) throws Exception {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            itemDAO.setSession(session);
            session.beginTransaction();
            itemDAO.update(
                    new Item(
                            item.getCode(),
                            item.getDescription(),
                            item.getUnitPrice(),
                            item.getQtyOnHand()));
            session.getTransaction().commit();
        }
    }

    @Override
    public void deleteItem(String itemCode) throws Exception {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            itemDAO.setSession(session);
            session.beginTransaction();

            if (orderDetailDAO.existsByItemCode(itemCode)) {
                throw new AlreadyExistsInOrderException("Item already exists in an order, hence unable to delete");
            }
            itemDAO.delete(itemCode);
            session.getTransaction().commit();
        }
    }

    @Override
    public List<ItemDTO> findAllItems() throws Exception {

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            itemDAO.setSession(session);
            session.beginTransaction();

            List<Item> allItems = itemDAO.findAll();
            List<ItemDTO> dtos = new ArrayList<>();
            for (Item item : allItems) {
                dtos.add(new ItemDTO(item.getCode(),
                        item.getDescription(),
                        item.getQtyOnHand(),
                        item.getUnitPrice()));
            }
            session.getTransaction().commit();
            return dtos;
        }
    }

    @Override
    public String getLastItemCode() throws Exception {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            itemDAO.setSession(session);
            session.beginTransaction();

            String lastItemCode = itemDAO.getLastItemCode();
            session.getTransaction().commit();
            return lastItemCode;
        }
    }

    @Override
    public ItemDTO findItem(String itemCode) throws Exception {

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            itemDAO.setSession(session);
            session.beginTransaction();

            Item item = itemDAO.find(itemCode);
            session.getTransaction().commit();
            return new ItemDTO(item.getCode(),
                    item.getDescription(),
                    item.getQtyOnHand(),
                    item.getUnitPrice());
        }
    }

    @Override
    public List<String> getAllItemCodes() throws Exception {

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            itemDAO.setSession(session);
            session.beginTransaction();

            List<Item> allItems = itemDAO.findAll();
            List<String> codes = new ArrayList<>();
            for (Item item : allItems) {
                codes.add(item.getCode());
            }
            session.getTransaction().commit();
            return codes;
        }
    }
}
