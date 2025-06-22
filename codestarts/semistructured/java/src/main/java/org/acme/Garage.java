package org.acme;

import jakarta.data.Sort;
import jakarta.data.page.Page;
import jakarta.data.page.PageRequest;
import jakarta.data.repository.*;
import org.eclipse.jnosql.mapping.NoSQLRepository;

/**
 * <p>Garage is an interface for managing car parking.</p>
 *
 * <p>It's using the Jakarta Data Specification capabilities.</p>
 *
 * <p></p>
 * <p>To use it, you need to inject it in your application like below:</p>
 *
 * <pre>
 * // By field injection
 * &#64Inject
 * Garage garage;
 *
 * // By constructor injection
 * &#64Inject
 * SomeService(Garage garage) {
 * </pre>
 *
 * <p>Example usage:</p>
 * <pre>
 * &#64Inject
 * Garage garage;
 * ...
 * Car car=...
 * car = garage.parking(car);
 * ...
 * var cars = garage.findByTransmission("Automatic", PageRequest.ofPage(1).size(2));
 * ...
 * garage.unpark(car);
 * ...
 * </pre>
 */
@Repository
public interface Garage extends NoSQLRepository<Car, String> {

    @Save
    Car parking(Car car);

    @Delete
    void unpark(Car car);

    @Find
    Page<Car> findByTransmission(@By("transmission") String transmission, PageRequest pageRequest, Sort...sorts);
}
