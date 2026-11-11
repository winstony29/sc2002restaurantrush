
public abstract class Seater extends Staff {

    public void seat(Customer c, Table t) {
        requireAvailable();

        if (c == null || t == null) {
            throw new IllegalMoveException(
                "Customer or table cannot be null."
            );
        }

        if (c.getStatus() != CustomerStatus.WAITING) {
            throw new IllegalMoveException(
                "Customer is not waiting to be seated."
            );
        }

        if (!t.isFree()) {
            throw new IllegalMoveException(
                "Table is already occupied."
            );
        }

        t.seat(c);
        c.seatAt(t);

        markActed();
    }
}

