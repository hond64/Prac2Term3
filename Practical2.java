//Wentzel
//Miguel
//4478677
//CSC212 2026 Practical 2 Term 3
import java.util.*;

public class CSC212_T3P2 {

    // Represents one trip
    static class Trip {
        String car;
        int passengers;
        int start;
        int end;

        Trip(String car, int passengers, String start, String end) {
            this.car = car;
            this.passengers = passengers;
            this.start = toMinutes(start);
            this.end = toMinutes(end);
        }
    }

    // Convert HH:MM to minutes
    static int toMinutes(String time) {
        String[] parts = time.split(":");
        return Integer.parseInt(parts[0]) * 60
                + Integer.parseInt(parts[1]);
    }

    // Convert minutes back to HH:MM
    static String formatTime(int minutes) {
        int hours = minutes / 60;
        int mins = minutes % 60;

        return String.format("%02d:%02d", hours, mins);
    }

    // Checks whether two trips can follow each other
    static boolean compatible(Trip previous, Trip next) {
        return next.start >= previous.end + 30;
    }

    /*
     * GREEDY APPROACH
     */
    static List<Trip> greedySchedule(List<Trip> trips) {

        List<Trip> sorted = new ArrayList<>(trips);

        sorted.sort(Comparator.comparingInt(t -> t.end));

        List<Trip> selected = new ArrayList<>();

        int lastEnd = -1;

        for (Trip trip : sorted) {

            if (selected.isEmpty() ||
                    trip.start >= lastEnd + 30) {

                selected.add(trip);
                lastEnd = trip.end;
            }
        }

        return selected;
    }

    /*
     * DYNAMIC PROGRAMMING / MEMOISATION
     */
    static List<Trip> dynamicSchedule(List<Trip> trips) {

        List<Trip> sorted = new ArrayList<>(trips);

        sorted.sort(Comparator.comparingInt(t -> t.end));

        int n = sorted.size();

        // p[i] = index of the last compatible trip
        int[] p = new int[n];

        for (int i = 0; i < n; i++) {
            p[i] = -1;

            for (int j = i - 1; j >= 0; j--) {

                if (sorted.get(j).end + 30
                        <= sorted.get(i).start) {

                    p[i] = j;
                    break;
                }
            }
        }

        /*
        dp stores max number
         */
        int[] dp = new int[n + 1];

        for (int i = 1; i <= n; i++) {

            Trip current = sorted.get(i - 1);

            int include = current.passengers;

            if (p[i - 1] != -1) {
                include += dp[p[i - 1] + 1];
            }

            int exclude = dp[i - 1];

            dp[i] = Math.max(include, exclude);
        }

        // Reconstruct selected trips
        List<Trip> selected = new ArrayList<>();

        int i = n;

        while (i > 0) {

            Trip current = sorted.get(i - 1);

            int include = current.passengers;

            if (p[i - 1] != -1) {
                include += dp[p[i - 1] + 1];
            }

            if (include > dp[i - 1]) {

                selected.add(current);
                i = p[i - 1] + 1;

            } else {
                i--;
            }
        }

        Collections.reverse(selected);

        /*
          Must 12 hour trip
         */
        List<Trip> best = new ArrayList<>();
        int bestPassengers = 0;

        // Try every possible starting trip
        for (int start = 0; start < n; start++) {

            List<Trip> possible = new ArrayList<>();

            int windowStart = sorted.get(start).start;
            int windowEnd = windowStart + 720;

            int total = 0;
            int lastEnd = -1;

            for (int j = start; j < n; j++) {

                Trip trip = sorted.get(j);

                if (trip.start < windowStart) {
                    continue;
                }

                if (trip.end > windowEnd) {
                    continue;
                }

                if (possible.isEmpty()
                        || trip.start >= lastEnd + 30) {

                    possible.add(trip);
                    total += trip.passengers;
                    lastEnd = trip.end;
                }
            }

            if (total > bestPassengers) {
                bestPassengers = total;
                best = possible;
            }
        }

        return best;
    }

    public static void main(String[] args) {

        List<Trip> trips = new ArrayList<>();

        trips.add(new Trip("A", 10, "08:00", "16:00"));
        trips.add(new Trip("B", 50, "01:00", "08:00"));
        trips.add(new Trip("C", 42, "13:00", "21:00"));
        trips.add(new Trip("D", 20, "22:00", "23:59"));
        trips.add(new Trip("E", 13, "02:00", "04:00"));
        trips.add(new Trip("F", 20, "07:00", "12:00"));
        trips.add(new Trip("G", 10, "15:00", "19:00"));
        trips.add(new Trip("H", 35, "17:00", "23:59"));
        trips.add(new Trip("I", 15, "05:00", "10:00"));
        trips.add(new Trip("J", 45, "11:00", "14:00"));
        trips.add(new Trip("K", 30, "00:01", "06:00"));
        trips.add(new Trip("L", 18, "20:00", "23:00"));
        trips.add(new Trip("M", 20, "13:00", "18:00"));

        // -----------------------------
        // GREEDY APPROACH
        // -----------------------------

        List<Trip> greedy = greedySchedule(trips);

        System.out.println("GREEDY APPROACH");

        for (Trip trip : greedy) {
            System.out.println(
                    trip.car + " : "
                    + formatTime(trip.start)
                    + " - "
                    + formatTime(trip.end)
            );
        }

        int greedyStart = greedy.get(0).start;
        int greedyEnd = greedy.get(greedy.size() - 1).end;

        System.out.println(
                "The optimal 12-hr window for the greedy approach is between "
                + formatTime(greedyStart)
                + " and "
                + formatTime(greedyEnd)
        );

        int greedyCommission = greedy.size() * 100;

        System.out.println(
                "His total commission if he earns R100 per trip is R"
                + greedyCommission
        );

        // -----------------------------
        // DYNAMIC PROGRAMMING
        // -----------------------------

        List<Trip> dpSchedule = dynamicSchedule(trips);

        int customerTotal = 0;

        for (Trip trip : dpSchedule) {
            customerTotal += trip.passengers;
        }

        int customerCommission = customerTotal * 10;

        System.out.println("\nDYNAMIC PROGRAMMING APPROACH");

        for (Trip trip : dpSchedule) {
            System.out.println(
                    trip.car + " : "
                    + formatTime(trip.start)
                    + " - "
                    + formatTime(trip.end)
                    + " : "
                    + trip.passengers
                    + " passengers"
            );
        }

        System.out.println(
                "His total commission if he were to earn R10 per customer is R"
                + customerCommission
        );

        System.out.print(
                "The cars to obtain the schedule based on largest customers are "
        );

        for (int i = 0; i < dpSchedule.size(); i++) {

            System.out.print(dpSchedule.get(i).car);

            if (i < dpSchedule.size() - 1) {
                System.out.print(", ");
            }
        }

        System.out.println();
    }
}
