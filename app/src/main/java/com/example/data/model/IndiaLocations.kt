package com.example.data.model

data class IndiaLocationEntry(
    val cityName: String,
    val stateOrUt: String,
    val latitude: Double,
    val longitude: Double,
    val isMajorHub: Boolean = false
) {
    fun toLocationInfo(): LocationInfo = LocationInfo(
        name = cityName,
        stateOrRegion = stateOrUt,
        country = "India",
        latitude = latitude,
        longitude = longitude,
        timezone = "Asia/Kolkata"
    )
}

object IndiaLocationsData {
    val ALL_INDIA_LOCATIONS: List<IndiaLocationEntry> = listOf(
        // Madhya Pradesh
        IndiaLocationEntry("Indore", "Madhya Pradesh", 22.7196, 75.8577, isMajorHub = true),
        IndiaLocationEntry("Bhopal", "Madhya Pradesh", 23.2599, 77.4126, isMajorHub = true),
        IndiaLocationEntry("Gwalior", "Madhya Pradesh", 26.2236, 78.1792),
        IndiaLocationEntry("Jabalpur", "Madhya Pradesh", 23.1815, 79.9864),
        IndiaLocationEntry("Ujjain", "Madhya Pradesh", 23.1765, 75.7885),

        // Maharashtra
        IndiaLocationEntry("Mumbai", "Maharashtra", 19.0760, 72.8777, isMajorHub = true),
        IndiaLocationEntry("Pune", "Maharashtra", 18.5204, 73.8567, isMajorHub = true),
        IndiaLocationEntry("Nagpur", "Maharashtra", 21.1458, 79.0882),
        IndiaLocationEntry("Nashik", "Maharashtra", 19.9975, 73.7898),
        IndiaLocationEntry("Aurangabad", "Maharashtra", 19.8762, 75.3433),

        // Delhi
        IndiaLocationEntry("New Delhi", "Delhi", 28.6139, 77.2090, isMajorHub = true),

        // Uttar Pradesh
        IndiaLocationEntry("Lucknow", "Uttar Pradesh", 26.8467, 80.9462, isMajorHub = true),
        IndiaLocationEntry("Varanasi", "Uttar Pradesh", 25.3176, 82.9739),
        IndiaLocationEntry("Agra", "Uttar Pradesh", 27.1767, 78.0081),
        IndiaLocationEntry("Kanpur", "Uttar Pradesh", 26.4499, 80.3319),
        IndiaLocationEntry("Noida", "Uttar Pradesh", 28.5355, 77.3910),

        // Karnataka
        IndiaLocationEntry("Bengaluru", "Karnataka", 12.9716, 77.5946, isMajorHub = true),
        IndiaLocationEntry("Mysuru", "Karnataka", 12.2958, 76.6394),
        IndiaLocationEntry("Mangaluru", "Karnataka", 12.9141, 74.8560),

        // Tamil Nadu
        IndiaLocationEntry("Chennai", "Tamil Nadu", 13.0827, 80.2707, isMajorHub = true),
        IndiaLocationEntry("Coimbatore", "Tamil Nadu", 11.0168, 76.9558),
        IndiaLocationEntry("Madurai", "Tamil Nadu", 9.9252, 78.1198),

        // West Bengal
        IndiaLocationEntry("Kolkata", "West Bengal", 22.5726, 88.3639, isMajorHub = true),
        IndiaLocationEntry("Darjeeling", "West Bengal", 27.0410, 88.2663),
        IndiaLocationEntry("Siliguri", "West Bengal", 26.7271, 88.3953),

        // Rajasthan
        IndiaLocationEntry("Jaipur", "Rajasthan", 26.9124, 75.7873, isMajorHub = true),
        IndiaLocationEntry("Udaipur", "Rajasthan", 24.5854, 73.7125),
        IndiaLocationEntry("Jodhpur", "Rajasthan", 26.2389, 73.0243),

        // Gujarat
        IndiaLocationEntry("Ahmedabad", "Gujarat", 23.0225, 72.5714, isMajorHub = true),
        IndiaLocationEntry("Surat", "Gujarat", 21.1702, 72.8311),
        IndiaLocationEntry("Vadodara", "Gujarat", 22.3072, 73.1812),

        // Telangana & Andhra Pradesh
        IndiaLocationEntry("Hyderabad", "Telangana", 17.3850, 78.4867, isMajorHub = true),
        IndiaLocationEntry("Visakhapatnam", "Andhra Pradesh", 17.6868, 83.2185),
        IndiaLocationEntry("Vijayawada", "Andhra Pradesh", 16.5062, 80.6480),

        // Kerala
        IndiaLocationEntry("Thiruvananthapuram", "Kerala", 8.5241, 76.9366),
        IndiaLocationEntry("Kochi", "Kerala", 9.9312, 76.2673, isMajorHub = true),
        IndiaLocationEntry("Kozhikode", "Kerala", 11.2588, 75.7804),

        // Punjab & Haryana
        IndiaLocationEntry("Chandigarh", "Punjab & Haryana", 30.7333, 76.7794, isMajorHub = true),
        IndiaLocationEntry("Amritsar", "Punjab", 31.6340, 74.8723),
        IndiaLocationEntry("Gurugram", "Haryana", 28.4595, 77.0266),

        // Himachal Pradesh & Uttarakhand
        IndiaLocationEntry("Shimla", "Himachal Pradesh", 31.1048, 77.1734),
        IndiaLocationEntry("Manali", "Himachal Pradesh", 32.2432, 77.1892),
        IndiaLocationEntry("Dehradun", "Uttarakhand", 30.3165, 78.0322),
        IndiaLocationEntry("Nainital", "Uttarakhand", 29.3919, 79.4542),

        // Bihar & Jharkhand
        IndiaLocationEntry("Patna", "Bihar", 25.5941, 85.1376),
        IndiaLocationEntry("Gaya", "Bihar", 24.7914, 85.0002),
        IndiaLocationEntry("Ranchi", "Jharkhand", 23.3441, 85.3096),
        IndiaLocationEntry("Jamshedpur", "Jharkhand", 22.8046, 86.2029),

        // Odisha & Chhattisgarh
        IndiaLocationEntry("Bhubaneswar", "Odisha", 20.2961, 85.8245),
        IndiaLocationEntry("Puri", "Odisha", 19.8135, 85.8312),
        IndiaLocationEntry("Raipur", "Chhattisgarh", 21.2514, 81.6296),

        // Goa
        IndiaLocationEntry("Panaji", "Goa", 15.4909, 73.8278),

        // Northeast States
        IndiaLocationEntry("Guwahati", "Assam", 26.1445, 91.7362, isMajorHub = true),
        IndiaLocationEntry("Shillong", "Meghalaya", 25.5788, 91.8933),
        IndiaLocationEntry("Gangtok", "Sikkim", 27.3389, 88.6065),
        IndiaLocationEntry("Agartala", "Tripura", 23.8315, 91.2868),
        IndiaLocationEntry("Imphal", "Manipur", 24.8170, 93.9368),
        IndiaLocationEntry("Aizawl", "Mizoram", 23.7271, 92.7176),
        IndiaLocationEntry("Kohima", "Nagaland", 25.6751, 94.1086),
        IndiaLocationEntry("Itanagar", "Arunachal Pradesh", 27.0844, 93.6053),

        // Jammu & Kashmir & Ladakh
        IndiaLocationEntry("Srinagar", "Jammu and Kashmir", 34.0837, 74.7973),
        IndiaLocationEntry("Jammu", "Jammu and Kashmir", 32.7266, 74.8570),
        IndiaLocationEntry("Leh", "Ladakh", 34.1526, 77.5771)
    )
}
