package de.techdev.trackr.domain;

import com.google.common.collect.Lists;
import de.techdev.trackr.core.security.AuthorityMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Moritz Schulze
 */
public abstract class AbstractDataOnDemand<S> {

    @Autowired
    protected CrudRepository<S, Long> repository;

    protected List<S> data;

    protected SecureRandom rnd;

    /**
     * If there's more elements in the repository than this method returns, no new elements will be generated.
     * Used to keep the admin account for the EmployeeRepository but also generate random ones.
     */
    protected int getExpectedElements() {
        return 0;
    }

    /**
     * Returns a random object from the pool.
     * <p>
     * Creates objects if none exist.
     *
     * @return A random object of the entity class.
     */
    public S getRandomObject() {
        init();
        S obj = data.get(rnd.nextInt(data.size()));
        Long id = getId(obj);
        //This might need admin rights
        SecurityContextHolder.getContext().setAuthentication(AuthorityMocks.adminAuthentication());
        S one = repository.findOne(id);
        SecurityContextHolder.getContext().setAuthentication(null);
        return one;
    }

    public Long getId(S obj) {
        try {
            Long id;
            Method getIdMethod = obj.getClass().getMethod("getId");
            id = (Long) getIdMethod.invoke(obj);
            return id;
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("Entity has no getId method");
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new IllegalStateException("Could not execute getId method");
        }
    }

    public AbstractDataOnDemand() {
        rnd = new SecureRandom();
    }

    public void init() {
        int from = 0;
        int to = 10;
        //Some repositories might have security annotations so we temporary acquire admin rights.
        SecurityContextHolder.getContext().setAuthentication(AuthorityMocks.adminAuthentication());
        data = Lists.newArrayList(repository.findAll());
        if (data == null) {
            throw new IllegalStateException("Find entries implementation for 'Component' illegally returned null");
        }
        if (data.size() > getExpectedElements()) {
            return;
        }

        data = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            S obj = getNewTransientObject(i);
            try {
                repository.save(obj);
            } catch (final ConstraintViolationException e) {
                final StringBuilder msg = new StringBuilder();
                for (Iterator<ConstraintViolation<?>> iter = e.getConstraintViolations().iterator(); iter.hasNext(); ) {
                    final ConstraintViolation<?> cv = iter.next();
                    msg.append("[").append(cv.getRootBean().getClass().getName()).append(".").append(cv.getPropertyPath()).append(": ").append(cv.getMessage())
                       .append(" (invalid value = ").append(cv.getInvalidValue()).append(")").append("]");
                }
                throw new IllegalStateException(msg.toString(), e);
            }
            data.add(obj);
        }
//        SecurityContextHolder.getContext().setAuthentication(null);
    }

    public abstract S getNewTransientObject(int i);
}
