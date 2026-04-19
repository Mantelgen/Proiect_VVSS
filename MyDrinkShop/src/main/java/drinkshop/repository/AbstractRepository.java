package drinkshop.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractRepository<ID, E>
        implements Repository<ID, E> {

    protected Map<ID, E> entities = new HashMap<>();

    @Override
    public E findOne(ID id) {
        return entities.get(id);
    }

    @Override
    public List<E> findAll() {
        return new ArrayList<>(entities.values());
    }

    @Override
    public E save(E entity) {
        entities.put(getId(entity), entity);
        return entity;
    }

    @Override
    public E delete(ID id) {
        return entities.remove(id);
    }

    @Override
    public E update(E entity) {
        if (!entities.containsKey(getId(entity))) {
            return null;
        }
        entities.put(getId(entity), entity);
        return entity;
    }

    protected abstract ID getId(E entity);
}
