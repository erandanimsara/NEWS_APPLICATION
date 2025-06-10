package com.example.newsapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView; // Make sure this is imported
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Color;
import android.graphics.Typeface;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.HashMap;
import java.util.Map;

public class home extends AppCompatActivity {

    private static final String TAG = "HomeActivity";

    // Inner class to hold data for each carousel
    private static class CarouselData {
        HorizontalScrollView scrollView;
        View dot1, dot2, dot3;
        int cardWidth = 0; // Initialize to 0, will be calculated on layout
        int spacing = 0;   // Initialize to 0, will be calculated on layout
        ViewTreeObserver.OnGlobalLayoutListener layoutListener;
        ViewTreeObserver.OnScrollChangedListener scrollListener;
        String name; // Add a name for easier debugging

        CarouselData(String name, HorizontalScrollView scrollView, View dot1, View dot2, View dot3) {
            this.name = name;
            this.scrollView = scrollView;
            this.dot1 = dot1;
            this.dot2 = dot2;
            this.dot3 = dot3;
        }
    }

    // Map to store CarouselData for different carousels (popular, academic, sport, events)
    private Map<String, CarouselData> carouselDataMap = new HashMap<>();

    // TextViews for "Read it more" links
    private TextView popularReadMore1, popularReadMore2, popularReadMore3;
    private TextView academicReadMore1, academicReadMore2, academicReadMore3;
    private TextView sportReadMore1, sportReadMore2, sportReadMore3;
    private TextView eventReadMore1, eventReadMore2, eventReadMore3;
    private TextView recommendedReadMore1, recommendedReadMore2, recommendedReadMore3, recommendedReadMore4;

    // UI elements for the menu
    private ImageView menuIcon;
    private LinearLayout menuOverlay;
    private LinearLayout menuContainer;
    private TextView menuItem1, menuItem2, menuItem3, menuItem4;

    // UI elements for the search bar
    private ImageView searchIcon;
    private LinearLayout titleContainer;
    private LinearLayout searchBarContainer;
    private EditText searchEditText;
    private ImageView closeSearchIcon;
    private boolean isSearchVisible = false;

    // UI elements for tabs and content sections
    private TextView tabPopular, tabAcademic, tabSport, tabEvents;
    private LinearLayout popularContentSection, academicContentSection, sportContentSection, eventsContentSection;

    // NEW: ImageView for the profile icon
    private ImageView profileIcon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Enable edge-to-edge display
        setContentView(R.layout.activity_home); // Set the layout for this activity

        // Apply window insets to handle system bars (status bar, navigation bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize carousels for different content sections
        initializeCarousel("popular", R.id.popularScrollView, R.id.popular_dot1, R.id.popular_dot2, R.id.popular_dot3, R.id.popularContentSection);
        initializeCarousel("academic", R.id.academicScrollView, R.id.academic_dot1, R.id.academic_dot2, R.id.academic_dot3, R.id.academicContentSection);
        initializeCarousel("sport", R.id.sportScrollView, R.id.sport_dot1, R.id.sport_dot2, R.id.sport_dot3, R.id.sportContentSection);
        initializeCarousel("events", R.id.eventsScrollView, R.id.event_dot1, R.id.event_dot2, R.id.event_dot3, R.id.eventsContentSection);

        // Initialize all "Read it more" TextViews
        initReadMoreTextViews();

        // Set up click listeners for "Read it more" TextViews
        setupReadMoreListeners();

        // Set up the side menu functionality
        setupMenu();
        // Set up the search bar functionality
        setupSearch();

        // Set up the tab navigation functionality
        setupTabs();

        // NEW: Initialize profileIcon and set its OnClickListener
        profileIcon = findViewById(R.id.profileIcon);
        profileIcon.setOnClickListener(v -> {
            // Create an Intent to start the Profile activity
            Intent intent = new Intent(home.this, profile.class); // IMPORTANT: Use 'Profile.class'
            startActivity(intent);
            Toast.makeText(home.this, "Going to Profile Screen", Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * Initializes a horizontal carousel with its scroll view and dot indicators.
     * Sets up global layout listener to determine card width and scroll listener to update dots.
     * @param name Unique name for the carousel (e.g., "popular", "academic")
     * @param scrollViewId Resource ID of the HorizontalScrollView
     * @param dot1Id Resource ID of the first dot indicator View
     * @param dot2Id Resource ID of the second dot indicator View
     * @param dot3Id Resource ID of the third dot indicator View
     * @param contentSectionId Resource ID of the LinearLayout containing the carousel cards
     */
    private void initializeCarousel(final String name, int scrollViewId, int dot1Id, int dot2Id, int dot3Id, int contentSectionId) {
        HorizontalScrollView scrollView = findViewById(scrollViewId);
        View dot1 = findViewById(dot1Id);
        View dot2 = findViewById(dot2Id);
        View dot3 = findViewById(dot3Id);
        LinearLayout innerLayout = findViewById(contentSectionId); // The direct parent of the cards

        CarouselData data = new CarouselData(name, scrollView, dot1, dot2, dot3);
        carouselDataMap.put(name, data);

        // Set the first dot as active initially for all carousels
        // This will be updated by the scroll listener once scrolling occurs
        setActiveDot(data, 1);

        // Listener to get the width of a single card and spacing after layout is drawn
        data.layoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // Ensure innerLayout has children and the first child has a valid width
                if (innerLayout.getChildCount() > 0) {
                    View firstChild = innerLayout.getChildAt(0);
                    // Only update if firstChild is not null and has a non-zero width
                    // This handles cases where the layout listener fires when the view is GONE (width=0)
                    if (firstChild != null && firstChild.getWidth() > 0) {
                        // Only update cardWidth and spacing if they are currently 0
                        // or if the firstChild's width has changed (e.g., orientation change)
                        if (data.cardWidth == 0 || data.cardWidth != firstChild.getWidth()) {
                            data.cardWidth = firstChild.getWidth(); // Get width of the first card

                            if (innerLayout.getChildCount() > 1) {
                                // Calculate spacing if there's more than one child
                                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) firstChild.getLayoutParams();
                                data.spacing = params.rightMargin;
                            }
                            Log.d(TAG, name + " Carousel layout initialized/updated: cardWidth=" + data.cardWidth + ", spacing=" + data.spacing);
                        }
                    } else {
                        Log.d(TAG, name + " Carousel layout: firstChild is null or width is 0. Will retry on next layout pass.");
                    }
                } else {
                    Log.d(TAG, name + " Carousel layout: innerLayout has no children yet.");
                }
            }
        };
        // Add the global layout listener to the scroll view's view tree observer
        scrollView.getViewTreeObserver().addOnGlobalLayoutListener(data.layoutListener);

        // Listener to update dots based on scroll position
        data.scrollListener = new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                // If cardWidth is 0, it means the layout hasn't been properly measured yet.
                // Return to prevent division by zero or incorrect dot calculations.
                if (data.cardWidth == 0) {
                    Log.w(TAG, data.name + " - Scroll detected, but cardWidth is 0. Dots not updated.");
                    return;
                }

                int scrollX = scrollView.getScrollX(); // Current horizontal scroll position
                int cardWidthWithSpacing = data.cardWidth + data.spacing;

                // Define scroll thresholds for changing active dots
                // These thresholds are set to be halfway through the visibility of each card
                int scrollThreshold1 = cardWidthWithSpacing / 2;
                int scrollThreshold2 = cardWidthWithSpacing + (cardWidthWithSpacing / 2);

                int currentActiveDot = 0;
                if (scrollX < scrollThreshold1) {
                    currentActiveDot = 1; // First dot active
                } else if (scrollX >= scrollThreshold1 && scrollX < scrollThreshold2) {
                    currentActiveDot = 2; // Second dot active
                } else {
                    currentActiveDot = 3; // Third dot active
                }
                setActiveDot(data, currentActiveDot);
                Log.d(TAG, data.name + " Scroll: scrollX=" + scrollX + ", activeDot=" + currentActiveDot);
            }
        };
        // Add the scroll listener to the scroll view
        scrollView.getViewTreeObserver().addOnScrollChangedListener(data.scrollListener);
    }

    /**
     * Sets the background resource for the active dot indicator in a carousel.
     * @param data The CarouselData object for the specific carousel
     * @param activeDot The index of the dot to set as active (1, 2, or 3)
     */
    private void setActiveDot(CarouselData data, int activeDot) {
        // Ensure dot views are not null before attempting to set background
        if (data.dot1 == null || data.dot2 == null || data.dot3 == null) {
            Log.w(TAG, data.name + " - Dot views are null, cannot set active dot.");
            return;
        }

        // Set background based on activeDot value
        data.dot1.setBackgroundResource(activeDot == 1 ? R.drawable.dot_active : R.drawable.dot_inactive);
        data.dot2.setBackgroundResource(activeDot == 2 ? R.drawable.dot_active : R.drawable.dot_inactive);
        data.dot3.setBackgroundResource(activeDot == 3 ? R.drawable.dot_active : R.drawable.dot_inactive);
    }

    /**
     * Initializes all "Read it more" TextViews by finding them by their IDs.
     */
    private void initReadMoreTextViews() {
        popularReadMore1 = findViewById(R.id.popularReadMore1);
        popularReadMore2 = findViewById(R.id.popularReadMore2);
        popularReadMore3 = findViewById(R.id.popularReadMore3);

        academicReadMore1 = findViewById(R.id.academicReadMore1);
        academicReadMore2 = findViewById(R.id.academicReadMore2);
        academicReadMore3 = findViewById(R.id.academicReadMore3);

        sportReadMore1 = findViewById(R.id.sportReadMore1);
        sportReadMore2 = findViewById(R.id.sportReadMore2);
        sportReadMore3 = findViewById(R.id.sportReadMore3);

        eventReadMore1 = findViewById(R.id.eventReadMore1);
        eventReadMore2 = findViewById(R.id.eventReadMore2);
        eventReadMore3 = findViewById(R.id.eventReadMore3);

        recommendedReadMore1 = findViewById(R.id.recommendedReadMore1);
        recommendedReadMore2 = findViewById(R.id.recommendedReadMore2);
        recommendedReadMore3 = findViewById(R.id.recommendedReadMore3);
        recommendedReadMore4 = findViewById(R.id.recommendedReadMore4);
    }

    /**
     * Sets up OnClickListener for all "Read it more" TextViews.
     * When clicked, it extracts the image resource ID and title text from the parent card
     * and passes them to the Fnews activity via an Intent.
     */
    private void setupReadMoreListeners() {
        View.OnClickListener readMoreClickListener = v -> {
            Intent intent = new Intent(home.this, Fnews.class);

            int imageResource = 0; // Default to 0
            String titleText = "";
            String newsDetailText = "";
            String newsCategory = ""; // New variable for news category (tab name)

            // Determine which "Read it more" TextView was clicked and assign corresponding data
            int clickedId = v.getId();
            if (clickedId == R.id.popularReadMore1) {
                imageResource = R.drawable.carousel_image_1; // REPLACE WITH YOUR ACTUAL IMAGE RESOURCE ID
                titleText = "Anniversary Celebration and Presidential Induction College of Pathologists of Sri Lanka"; // REPLACE WITH YOUR ACTUAL TITLE
                newsDetailText = "The College of Pathologists of Sri Lanka celebrated its anniversary and inducted a new president. The event highlighted the college's achievements and future goals in the field of pathology.";
                newsCategory = "Popular";
            } else if (clickedId == R.id.popularReadMore2) {
                imageResource = R.drawable.carousel_image_2; // REPLACE WITH YOUR ACTUAL IMAGE RESOURCE ID
                titleText = "General Convocation 2024 By University of Colombo University of Colombo "; // REPLACE WITH YOUR ACTUAL TITLE
                newsDetailText = "The University of Colombo held its General Convocation for the year 2024, graduating thousands of students across various disciplines. The ceremony was a grand event, celebrating academic excellence and student achievements.";
                newsCategory = "Popular";
            } else if (clickedId == R.id.popularReadMore3) {
                imageResource = R.drawable.carousel_image_3; // REPLACE WITH YOUR ACTUAL IMAGE RESOURCE ID
                titleText = "Professor Priyani Amarathunga Inducted as the President of the College of Pathologists of Sri Lanka"; // REPLACE WITH YOUR ACTUAL TITLE
                newsDetailText = "Professor Priyani Amarathunga was officially inducted as the new President of the College of Pathologists of Sri Lanka. Her induction marks a new chapter for the college, with a focus on advancing research and medical practices.";
                newsCategory = "Popular";
            } else if (clickedId == R.id.academicReadMore1) {
                imageResource = R.drawable.academic_image_1; // REPLACE WITH YOUR ACTUAL IMAGE RESOURCE ID
                titleText = "New Research Publication"; // REPLACE WITH YOUR ACTUAL TITLE
                newsDetailText = "A groundbreaking new research paper has been published by the Faculty of Science, detailing significant advancements in sustainable energy solutions. The publication is expected to have a major impact on the field.";
                newsCategory = "Academic";
            } else if (clickedId == R.id.academicReadMore2) {
                imageResource = R.drawable.academic_image_2; // REPLACE WITH YOUR ACTUAL IMAGE RESOURCE ID
                titleText = "Upcoming Workshop Details"; // REPLACE WITH YOUR ACTUAL TITLE
                newsDetailText = "The Department of Computer Science announced details for an upcoming workshop on Artificial Intelligence and Machine Learning, scheduled for next month. Registration is now open for all interested students and professionals.";
                newsCategory = "Academic";
            } else if (clickedId == R.id.academicReadMore3) {
                imageResource = R.drawable.academic_image_3; // REPLACE WITH YOUR ACTUAL IMAGE RESOURCE ID
                titleText = "Scholarship Opportunities"; // REPLACE WITH YOUR ACTUAL TITLE
                newsDetailText = "New scholarship opportunities are now available for postgraduate students in various fields of study. These scholarships aim to support academic excellence and foster future leaders in research and innovation.";
                newsCategory = "Academic";
            } else if (clickedId == R.id.sportReadMore1) {
                imageResource = R.drawable.sport_image_1; // REPLACE WITH YOUR ACTUAL IMAGE RESOURCE ID
                titleText = "Inter-University Games Victory"; // REPLACE WITH YOUR ACTUAL TITLE
                newsDetailText = "The university's athletic team secured a resounding victory at the annual Inter-University Games, bringing home multiple gold medals and trophies. The win is a testament to the hard work and dedication of the athletes and coaches.";
                newsCategory = "Sport";
            } else if (clickedId == R.id.sportReadMore2) {
                imageResource = R.drawable.sport_image_2; // REPLACE WITH YOUR ACTUAL IMAGE RESOURCE ID
                titleText = "New Sports Complex Opening"; // REPLACE WITH YOUR ACTUAL TITLE
                newsDetailText = "A state-of-the-art sports complex was inaugurated today, providing students with modern facilities for various sports. The complex includes a new gymnasium, swimming pool, and outdoor courts.";
                newsCategory = "Sport";
            } else if (clickedId == R.id.sportReadMore3) {
                imageResource = R.drawable.sport_image_3; // REPLACE WITH YOUR ACTUAL IMAGE RESOURCE ID
                titleText = "Athlete Spotlight"; // REPLACE WITH YOUR ACTUAL TITLE
                newsDetailText = "This month's athlete spotlight features national-level swimmer, Kasun Perera, who recently broke a national record. Learn about his training regimen, motivations, and aspirations for the future.";
                newsCategory = "Sport";
            } else if (clickedId == R.id.eventReadMore1) {
                imageResource = R.drawable.event_image_1; // REPLACE WITH YOUR ACTUAL IMAGE RESOURCE ID
                titleText = "Annual Cultural Festival"; // REPLACE WITH YOUR ACTUAL TITLE
                newsDetailText = "The university's annual cultural festival was a vibrant display of talent, featuring traditional dances, musical performances, and art exhibitions. The event brought together students from diverse backgrounds.";
                newsCategory = "Events";
            } else if (clickedId == R.id.eventReadMore2) {
                imageResource = R.drawable.event_image_2; // REPLACE WITH YOUR ACTUAL IMAGE RESOURCE ID
                titleText = "Guest Lecture Series"; // REPLACE WITH YOUR ACTUAL TITLE
                newsDetailText = "A new guest lecture series commenced this week, featuring renowned speakers from various industries and academic fields. The series aims to provide students with insights into current trends and career opportunities.";
                newsCategory = "Events";
            } else if (clickedId == R.id.eventReadMore3) {
                imageResource = R.drawable.event_image_3; // REPLACE WITH YOUR ACTUAL IMAGE RESOURCE ID
                titleText = "Alumni Meetup Information"; // REPLACE WITH YOUR ACTUAL TITLE
                newsDetailText = "The annual alumni meetup is scheduled for next month, inviting all former students to reconnect with their alma mater and network with fellow graduates. Special events and guest speakers are planned.";
                newsCategory = "Events";
            } else if (clickedId == R.id.recommendedReadMore1) {
                imageResource = R.drawable.recommended_image_1; // REPLACE WITH YOUR ACTUAL IMAGE RESOURCE ID
                titleText = "Opening ceremony of the ‘Hydrotherapy and Recovery Room’ of the Center for Sport and Exercise Medicine (CSEM)"; // REPLACE WITH YOUR ACTUAL TITLE
                newsDetailText = "The University of Colombo officially initiated its activities for the New Year on January 1, 2025, at the College House premises. A special event marked the occasion, attended by the Vice Chancellor, Senior Professor (Chair) H D Karunaratne along with the Rector of Sri Palee Campus, Dr P. Mananamaheva, Deans of Faculties, Directors of institutes and the UCSC, the Registrar, Mrs K C Sanjeevani Perera, the Librarian, Dr Pradespa Wijetunge, the Bursar, Ms J T L Dharmasena and all other staff members of the University.\n\nThe ceremony began with the hoisting of the National and University flags, followed by the singing of the National Anthem. The event also aligned with the 'Clean Sri Lanka' initiative launched by the government. Efforts were made to integrate this initiative into the national New Year celebrations, emphasizing the University's dedication to sustainability and national development.";
                newsCategory = "Recommended"; // Or "Popular" if it fits that category
            } else if (clickedId == R.id.recommendedReadMore2) {
                imageResource = R.drawable.recommended_image_2; // REPLACE WITH YOUR ACTUAL IMAGE RESOURCE ID
                titleText = "Professor Priyani Amarathunga"; // REPLACE WITH YOUR ACTUAL TITLE
                newsDetailText = "Professor Priyani Amarathunga, the Head of the Department of Pathology and the Director of the Centre for Diagnosis and Research in Cancer, Faculty of Medicine was inducted as the President of the College of Pathologists of Sri Lanka on 12th March 2025 at a function held at Monarch Imperial Hotel, Sri Jayawardenapura. The College of Pathologists of Sri Lanka, established in 1975 celebrates its 50th anniversary this year under the theme of commemorating the past, excelling in the present and aspiring for the future.";
                newsCategory = "Recommended"; // Or "Popular" if it fits that category
            } else if (clickedId == R.id.recommendedReadMore3) {
                imageResource = R.drawable.recommended_image_3; // REPLACE WITH YOUR ACTUAL IMAGE RESOURCE ID
                titleText = "The Faculty of Education celebrates its Golden Jubilee in 2025"; // REPLACE WITH YOUR ACTUAL TITLE
                newsDetailText = "The Faculty of Education celebrates its Golden Jubilee in 2025, marking 50 years of excellence in fostering teacher professionalism and shaping the future of education in Sri Lanka. To kick off this historic year, a multi-religious blessing ceremony was held on 1st January 2025 at the Faculty. The ceremony was presided over by Senior Professor (Chair) H D Karunaratne, the Vice Chancellor of the University. This momentous occasion was just the beginning of a yearlong celebration, with an exciting calendar of events planned throughout 2025.";
                newsCategory = "Recommended"; // Or "Academic" if it fits that category
            } else if (clickedId == R.id.recommendedReadMore4) {
                imageResource = R.drawable.recommended_image_4; // REPLACE WITH YOUR ACTUAL IMAGE RESOURCE ID
                titleText = "Distinguished guests who attended the function included"; // REPLACE WITH YOUR ACTUAL TITLE
                newsDetailText = "Distinguished guests who attended the function included, Emeritus Professor Neelakanthi Ratnatunga, University of Peradeniya, the Chief Guest, Emeritus Professor Chandu de Silva, the Guest Speaker, Professor Vidya Jothi Vajira Dissanayake, the Dean of the Faculty of Medicine, Emeritus Professor Jennifer Perera, former Dean of the Faculty of Medicine, Emeritus Professor L R Amarasekera and Emeritus Professor Saroj Jayasinghe. Professor Chandu de Silva delivered the 50th Anniversary Lecture on “The Final Diagnosis: A 50-year Odyssey” highlighting the important events in the 50 years’ journey of the College of Pathologists of Sri Lanka. The vote of thanks was delivered by the Honorary Secretary Dr Lalani J De Silva.";
                newsCategory = "Recommended"; // Or "Academic" if it fits that category
            }


            if (imageResource != 0 && !titleText.isEmpty() && !newsDetailText.isEmpty() && !newsCategory.isEmpty()) {
                // Put data into the Intent
                intent.putExtra("imageResource", imageResource);
                intent.putExtra("titleText", titleText);
                intent.putExtra("newsDetailText", newsDetailText);
                intent.putExtra("newsCategory", newsCategory); // Pass the news category (tab name)

                // Start the Fnews activity
                startActivity(intent);
            } else {
                Toast.makeText(home.this, "Could not retrieve news data for this item.", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Failed to get image resource, title, detail or category for clicked 'Read it more'. Clicked ID: " + getResources().getResourceEntryName(clickedId));
            }
        };

        // Assign the click listener to all "Read it more" TextViews
        setClickListener(popularReadMore1, readMoreClickListener);
        setClickListener(popularReadMore2, readMoreClickListener);
        setClickListener(popularReadMore3, readMoreClickListener);

        setClickListener(academicReadMore1, readMoreClickListener);
        setClickListener(academicReadMore2, readMoreClickListener);
        setClickListener(academicReadMore3, readMoreClickListener);

        setClickListener(sportReadMore1, readMoreClickListener);
        setClickListener(sportReadMore2, readMoreClickListener);
        setClickListener(sportReadMore3, readMoreClickListener);

        setClickListener(eventReadMore1, readMoreClickListener);
        setClickListener(eventReadMore2, readMoreClickListener);
        setClickListener(eventReadMore3, readMoreClickListener);

        setClickListener(recommendedReadMore1, readMoreClickListener);
        setClickListener(recommendedReadMore2, readMoreClickListener);
        setClickListener(recommendedReadMore3, readMoreClickListener);
        setClickListener(recommendedReadMore4, readMoreClickListener);
    }

    /**
     * Helper method to safely set an OnClickListener on a TextView.
     * @param textView The TextView to set the listener on.
     * @param listener The OnClickListener to set.
     */
    private void setClickListener(TextView textView, View.OnClickListener listener) {
        if (textView != null) {
            textView.setOnClickListener(listener);
        } else {
            Log.e(TAG, "Attempted to set click listener on a null TextView.");
        }
    }

    /**
     * Sets up the functionality for the side navigation menu.
     * Handles opening and closing the menu overlay and click actions for menu items.
     */
    private void setupMenu() {
        menuIcon = findViewById(R.id.menuIcon);
        menuOverlay = findViewById(R.id.menuOverlay);
        menuContainer = findViewById(R.id.menuContainer);
        menuItem1 = findViewById(R.id.menuItem1);
        menuItem2 = findViewById(R.id.menuItem2);
        menuItem3 = findViewById(R.id.menuItem3);
        menuItem4 = findViewById(R.id.menuItem4);

        // Open menu when menu icon is clicked
        menuIcon.setOnClickListener(v -> {
            menuOverlay.setVisibility(View.VISIBLE);
        });

        // Close menu when overlay (outside menu container) is clicked
        menuOverlay.setOnClickListener(v -> {
            menuOverlay.setVisibility(View.GONE);
        });

        // Prevent menu container clicks from closing the menu
        menuContainer.setOnClickListener(v -> {
            // Do nothing, consume the click event
        });

        // Set click listeners for individual menu items
        menuItem1.setOnClickListener(v -> {
            Toast.makeText(this, "Home clicked", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(home.this, home.class);
            startActivity(intent);
            menuOverlay.setVisibility(View.GONE);
        });
        menuItem2.setOnClickListener(v -> {
            Toast.makeText(this, "Settings clicked", Toast.LENGTH_SHORT).show();
            // Assuming you have a Settings activity
            // Intent intent = new Intent(home.this, SettingsActivity.class);
            // startActivity(intent);
            menuOverlay.setVisibility(View.GONE);
        });
        menuItem3.setOnClickListener(v -> {
            Toast.makeText(this, "About clicked", Toast.LENGTH_SHORT).show();
            // Assuming you have an About activity
            // Intent intent = new Intent(home.this, AboutActivity.class);
            // startActivity(intent);
            menuOverlay.setVisibility(View.GONE);
        });
        menuItem4.setOnClickListener(v -> {
            Toast.makeText(this, "Logout clicked", Toast.LENGTH_SHORT).show();
            // Assuming you have a Login activity to return to
            // Intent intent = new Intent(home.this, LoginActivity.class);
            // startActivity(intent);
            // finish(); // Close current activity
            menuOverlay.setVisibility(View.GONE);
        });
    }

    /**
     * Sets up the functionality for the search bar.
     * Handles showing/hiding the search bar and performing a search.
     */
    private void setupSearch() {
        searchIcon = findViewById(R.id.searchIcon);
        titleContainer = findViewById(R.id.titleContainer);
        searchBarContainer = findViewById(R.id.searchBarContainer);
        searchEditText = findViewById(R.id.searchEditText);
        closeSearchIcon = findViewById(R.id.closeSearchIcon);

        // Toggle search bar visibility and perform search
        searchIcon.setOnClickListener(v -> {
            if (isSearchVisible) {
                // If search bar is visible, perform search
                String query = searchEditText.getText().toString().trim();
                if (!query.isEmpty()) {
                    Toast.makeText(this, "Searching for: " + query, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Please enter a search query", Toast.LENGTH_SHORT).show();
                }
            } else {
                // If search bar is hidden, show it
                titleContainer.setVisibility(View.GONE);
                searchBarContainer.setVisibility(View.VISIBLE);
                searchIcon.setImageResource(R.drawable.ic_search); // Ensure search icon remains consistent
                isSearchVisible = true;
            }
        });

        // Close search bar when close icon is clicked
        closeSearchIcon.setOnClickListener(v -> {
            searchBarContainer.setVisibility(View.GONE);
            titleContainer.setVisibility(View.VISIBLE);
            searchEditText.setText(""); // Clear search text
            searchIcon.setImageResource(R.drawable.ic_search); // Reset search icon
            isSearchVisible = false;
        });
    }

    /**
     * Sets up the tab navigation functionality.
     * Handles changing active tabs and showing/hiding corresponding content sections.
     */
    private void setupTabs() {
        tabPopular = findViewById(R.id.tabPopular);
        tabAcademic = findViewById(R.id.tabAcademic);
        tabSport = findViewById(R.id.tabSport);
        tabEvents = findViewById(R.id.tabEvents);

        popularContentSection = findViewById(R.id.popularContentSection);
        academicContentSection = findViewById(R.id.academicContentSection);
        sportContentSection = findViewById(R.id.sportContentSection);
        eventsContentSection = findViewById(R.id.eventsContentSection);

        // Set Popular tab as active initially and show its content
        setActiveTab(tabPopular);
        popularContentSection.setVisibility(View.VISIBLE);

        // Set click listeners for each tab
        tabPopular.setOnClickListener(v -> {
            setActiveTab(tabPopular);
            showContentSection(popularContentSection);
        });

        tabAcademic.setOnClickListener(v -> {
            setActiveTab(tabAcademic);
            showContentSection(academicContentSection);
        });

        tabSport.setOnClickListener(v -> {
            setActiveTab(tabSport);
            showContentSection(sportContentSection);
        });

        tabEvents.setOnClickListener(v -> {
            setActiveTab(tabEvents);
            showContentSection(eventsContentSection);
        });
    }

    /**
     * Updates the appearance of tabs to highlight the active one.
     * @param activeTab The TextView representing the currently active tab.
     */
    private void setActiveTab(TextView activeTab) {
        // Reset all tabs to inactive style
        tabPopular.setTextColor(Color.parseColor("#888888"));
        tabPopular.setTypeface(null, Typeface.NORMAL);
        tabAcademic.setTextColor(Color.parseColor("#888888"));
        tabAcademic.setTypeface(null, Typeface.NORMAL);
        tabSport.setTextColor(Color.parseColor("#888888"));
        tabSport.setTypeface(null, Typeface.NORMAL);
        tabEvents.setTextColor(Color.parseColor("#888888"));
        tabEvents.setTypeface(null, Typeface.NORMAL);

        // Set the active tab to active style
        activeTab.setTextColor(Color.parseColor("#000000"));
        activeTab.setTypeface(null, Typeface.BOLD);
    }

    /**
     * Shows only the specified content section and hides all others.
     * @param sectionToShow The LinearLayout content section to make visible.
     */
    private void showContentSection(LinearLayout sectionToShow) {
        // Hide all content sections
        popularContentSection.setVisibility(View.GONE);
        academicContentSection.setVisibility(View.GONE);
        sportContentSection.setVisibility(View.GONE);
        eventsContentSection.setVisibility(View.GONE);

        // Show the selected content section
        sectionToShow.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove global layout and scroll listeners to prevent memory leaks
        for (Map.Entry<String, CarouselData> entry : carouselDataMap.entrySet()) {
            CarouselData data = entry.getValue();
            if (data != null && data.scrollView != null) {
                ViewTreeObserver observer = data.scrollView.getViewTreeObserver();
                if (observer.isAlive()) {
                    if (data.layoutListener != null) {
                        observer.removeOnGlobalLayoutListener(data.layoutListener);
                        data.layoutListener = null; // Clear the reference to prevent leaks
                    }
                    if (data.scrollListener != null) {
                        observer.removeOnScrollChangedListener(data.scrollListener);
                        data.scrollListener = null; // Clear the reference to prevent leaks
                    }
                }
            }
        }
        carouselDataMap.clear(); // Clear the map
    }
}
