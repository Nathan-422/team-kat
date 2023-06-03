import { User } from "./user";
import { Restaurant } from "./restaurant";

export class Event {

    id: number;
    entryCode: string; // six characters. Numbers too?
    location: string; // zip code? Address?
    searchRadius: string;  // meters?
    restaurants: Array<Restaurant>;
    currentUser: User;
    otherUsers: Array<User>;
    mealTime: Date;

    constructor(
        id: number,
        location: string,
        date: Date,
        searchRadius: string, 
        restaurants: Array<Restaurant>,
        entryCode: string,
        currentUser: User,
        otherUsers: Array<User>,
        ) {
            this.id = id;
            this.location = location;
            this.mealTime = date;
            this.searchRadius = searchRadius;
            this.restaurants = restaurants;
            this.entryCode = entryCode;
            this.currentUser = currentUser;
            this.otherUsers = otherUsers;
        }

}
